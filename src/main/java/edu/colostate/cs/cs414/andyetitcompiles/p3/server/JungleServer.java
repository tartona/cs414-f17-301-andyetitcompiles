package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.GameRecord;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Tournament;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleServer {
	Server server;
	DatabaseManagerSQL database;
	Map<Integer, ServerGameController> games;
	Map<String, Tournament> tournaments;
	int gameCounter;

	public JungleServer(DatabaseManagerSQL database) throws IOException {
		server = new Server() {
			// Each time a new connection comes into the server, replace it with a JungleClientConnection (which extends connection)
			protected Connection newConnection() {
				return new JungleClientConnection();
			}
		};
		this.database = database;
		networkSetup();
	}

	public JungleServer() throws IOException {
		games = new HashMap<Integer, ServerGameController>();
		tournaments = new HashMap<String, Tournament>();
		gameCounter = 0;
		server = new Server() {
			protected Connection newConnection() {
				return new JungleClientConnection();
			}
		};

		//try to connect to database, if this fails use temp database
		try {
			this.database = new DatabaseManagerSQL();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.out.println("Database not connected");
			System.exit(2);
		}
		networkSetup();
	}
	public JungleServer(int port) throws IOException {
		server = new Server() {
			protected Connection newConnection() {
				return new JungleClientConnection();
			}
		};

		//try to connect to database, if this fails use temp database
		try {
			this.database = new DatabaseManagerSQL();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.out.println("Database not connected");
			System.exit(2);
		}
		networkSetup();
	}

	public JungleServer(String dbLocation, String dbUsername, String dbPassword) throws IOException {
		server = new Server() {
			protected Connection newConnection() {
				return new JungleClientConnection();
			}
		};

		//try to connect to database, if this fails use temp database
		try {
			this.database = new DatabaseManagerSQL(dbLocation,dbUsername,dbPassword);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.out.println("Database not connected");
			System.exit(2);
		}
		networkSetup();
	}

	private void networkSetup() throws IOException {
		Network.register(server);
		System.out.println(Network.host);
		System.out.println(Network.port);
		JungleServer jServer = this;
		server.addListener(new Listener() {
			public void received(Connection c, Object object) {
				// We know every connection is a JungleClientConnection
				JungleClientConnection jClient = (JungleClientConnection)c;
				if (object instanceof LoginRequest) {
					LoginRequest loginRequest = (LoginRequest) object;
					LoginResponse loginResp = database.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
					c.sendTCP(loginResp);
					// If the login was successful, set the user of the JungleClientConnection that logged in
					if(loginResp.successful()) {
						jClient.setUser(loginResp.getUser()); // Set that connection to a User
						sendActiveGames(jClient); // Send all the active games the user was playing
					}
				}
				if (object instanceof RegisterRequest) {
					RegisterRequest registerRequest = (RegisterRequest) object;
					User user = new User(registerRequest.getEmail(), registerRequest.getNickname(),
							registerRequest.getPassword());
					if (validEmail(user.getEmail())) {
						System.out.println("Sending RegisterResponse");
						c.sendTCP(database.registerUser(user));
					} else {
						c.sendTCP(new RegisterResponse(false, "Invalid Email Address"));
					}
				}
				if (object instanceof UnregisterRequest) {
					UnregisterRequest unregisterRequest = (UnregisterRequest) object;
					c.sendTCP(database.unRegisterUser(unregisterRequest.getEmail(), unregisterRequest.getPassword()));
				}
				if (object instanceof UserRequest) {
					UserRequest userRequest = (UserRequest) object;
					c.sendTCP(database.findUser(userRequest.getNickname()));
				}
				// Invite request received from a client
				if (object instanceof InviteRequest) {
					InviteRequest inviteReq = (InviteRequest)object;
					// Try to find the user to be invited in the list of active connections
					JungleClientConnection recipient = null;
					for (Connection conn : server.getConnections()) {
						JungleClientConnection jConn = (JungleClientConnection)conn;
						// Check this connection to see if it is logged in and matches the requested user
						if (jConn.getUser() != null && jConn.getUser().equals(inviteReq.getInvitee())) {
							System.out.println("Found user to invite");
							recipient = jConn;
							break;
						}
					}
					// Didn't find the user, send a response to the inviter telling them so
					if(recipient == null) {
						c.sendTCP(new InviteResponse(false, null, null, "Server: Could not find user. They are either offline or don't exist"));
					}
					// Otherwise, forward the request to the specified user.
					else {
						recipient.sendTCP(inviteReq);
					}
				}
				// Invite response received from a client
				if (object instanceof InviteResponse) {
					InviteResponse inviteResp = (InviteResponse)object;
					// If the response is accepted, forward it to the other user, and create a game instance for them to play on
					if(inviteResp.isAccepted()) {
						// Find the 2 connections that are associated with inviter and invitee
						JungleClientConnection inviter = null;
						JungleClientConnection invitee = null;
						for(Connection conn : server.getConnections()) {
							if(((JungleClientConnection) conn).getUser() != null && ((JungleClientConnection)conn).getUser().equals(inviteResp.getInviter()))
								inviter = (JungleClientConnection)conn;
							else if(((JungleClientConnection)conn).getUser() != null && ((JungleClientConnection)conn).getUser().equals(inviteResp.getInvitee()))
								invitee = (JungleClientConnection)conn;
						}
						// It found both connections, send the response and create the game, otherwise do nothing for now.
						if(inviter != null && invitee != null) {
							// send the response
							inviter.sendTCP(inviteResp);
							// Create the server game instance
							ServerGameController newGame = new ServerGameController(gameCounter, inviter, invitee, jServer);
							games.put(gameCounter, newGame);
							database.addGame(gameCounter, inviter.getUser().getId(), invitee.getUser().getId(), new Timestamp(System.currentTimeMillis()), 1, null);
							// Send the game info to the clients
							GameInstance inviterMessage = new GameInstance(gameCounter, invitee.getUser(), Color.WHITE, null);
							GameInstance inviteeMessage = new GameInstance(gameCounter, inviter.getUser(), Color.BLACK, null);
							inviter.sendTCP(inviterMessage);
							invitee.sendTCP(inviteeMessage);
							gameCounter++;
							newGame.startGame();
						}
					}
					// The invite response was not accepted, forward to inviter
					else {
						for(Connection conn : server.getConnections()) {
							if(((JungleClientConnection) conn).getUser().equals(inviteResp.getInviter())) {
								conn.sendTCP(inviteResp);
								break;
							}
						}
					}
				}
				if (object instanceof TournamentMessage) {
					TournamentMessage tournamentMsg = (TournamentMessage)object;
					TournamentMessage tmntResponse;
					if(tournamentMsg.getType() == TournamentMessageType.CREATE) {
						if(tournaments.containsKey(tournamentMsg.getTournamentID())){
							tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "The tournament ID "+tournamentMsg.getTournamentID()+" is already exists.");
							c.sendTCP(tmntResponse);
						}else{
							Tournament tnmt = new Tournament(tournamentMsg.getTournamentID(), tournamentMsg.getTournamentOwner(), tournamentMsg.getMaxPlayer());
							tournaments.put(tournamentMsg.getTournamentID(), tnmt);
							tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "The tournament successfully created");
							c.sendTCP(tmntResponse);
						}
					}
					else if(tournamentMsg.getType() == TournamentMessageType.JOIN) {
						if(tournaments.containsKey(tournamentMsg.getTournamentID())){
							int returnVal = tournaments.get(tournamentMsg.getTournamentID()).addPlayer(jClient);
							if(returnVal==0) {
								tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "You cannot join the tournament "+tournamentMsg.getTournamentID());
								c.sendTCP(tmntResponse);
							}else {
								tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "You've enrolled in the tournament "+tournamentMsg.getTournamentID());
								c.sendTCP(tmntResponse);
								for(JungleClientConnection conn : tournaments.get(tournamentMsg.getTournamentID()).getPlayerConnections()) {
									if(!conn.getUser().getNickname().equals(tournaments.get(tournamentMsg.getTournamentID()).getTournamentOwner())){
										tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "New player joined to the tournament "+tournamentMsg.getTournamentID()+"("+tournaments.get(tournamentMsg.getTournamentID()).getNumPlayers()+"/"+tournaments.get(tournamentMsg.getTournamentID()).getMaxPlayer()+")");
										conn.sendTCP(tmntResponse);
									}
								}
								for(Connection conn : server.getConnections()) {
									if(((JungleClientConnection) conn).getUser() != null && ((JungleClientConnection)conn).getUser().getNickname().equals(tournaments.get(tournamentMsg.getTournamentID()).getTournamentOwner())){
										tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "New player joined to your tournament "+tournamentMsg.getTournamentID()+"("+tournaments.get(tournamentMsg.getTournamentID()).getNumPlayers()+"/"+tournaments.get(tournamentMsg.getTournamentID()).getMaxPlayer()+")");
										conn.sendTCP(tmntResponse);
									}
								}
							}

						}else{
							tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "The tournament ID does not exists");
							c.sendTCP(tmntResponse);
						}
					}
					else if(tournamentMsg.getType() == TournamentMessageType.LEAVE) {
						if(tournaments.containsKey(tournamentMsg.getTournamentID())){
							int returnVal = tournaments.get(tournamentMsg.getTournamentID()).removePlayer(jClient);
							if(returnVal==0) {
								tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "You cannot leave the tournament "+tournamentMsg.getTournamentID());
								c.sendTCP(tmntResponse);
							}else {
								tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "You've removed from the tournament "+tournamentMsg.getTournamentID());
								c.sendTCP(tmntResponse);
								for(Connection conn : server.getConnections()) {
									if(((JungleClientConnection) conn).getUser() != null && ((JungleClientConnection)conn).getUser().getNickname().equals(tournaments.get(tournamentMsg.getTournamentID()).getTournamentOwner())){
										tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "Someone left from your tournament "+tournamentMsg.getTournamentID()+"("+tournaments.get(tournamentMsg.getTournamentID()).getNumPlayers()+"/"+tournaments.get(tournamentMsg.getTournamentID()).getMaxPlayer()+")");
										conn.sendTCP(tmntResponse);
									}
								}
							}
						}else{
							tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "The tournament ID does not exists");
							c.sendTCP(tmntResponse);
						}
					}
					else if(tournamentMsg.getType() == TournamentMessageType.START) {
						if(tournaments.containsKey(tournamentMsg.getTournamentID()) && tournaments.get(tournamentMsg.getTournamentID()).getTournamentOwner().equals(tournamentMsg.getTournamentOwner())){
							int returnVal = tournaments.get(tournamentMsg.getTournamentID()).start();
							if(returnVal==0){
								tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "You cannot start the tournament "+tournamentMsg.getTournamentID());
								c.sendTCP(tmntResponse);
							}else {
								tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "You've started the tournament "+tournamentMsg.getTournamentID());
								c.sendTCP(tmntResponse);
								for(JungleClientConnection conn : tournaments.get(tournamentMsg.getTournamentID()).getPlayerConnections()) {
									tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "New round of Tournament("+tournamentMsg.getTournamentID()+") has begun\n"+tournaments.get(tournamentMsg.getTournamentID()).getTournamentHistory());
									conn.sendTCP(tmntResponse);
								}
								createTournamentGames(tournamentMsg.getTournamentID());
							}

						}else{
							tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "The tournament ID does not exists");
							c.sendTCP(tmntResponse);
						}
					}
					else if(tournamentMsg.getType() == TournamentMessageType.END) {
						if(tournaments.containsKey(tournamentMsg.getTournamentID()) && tournaments.get(tournamentMsg.getTournamentID()).getTournamentOwner().equals(tournamentMsg.getTournamentOwner())){
							tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "You've ended the tournament "+tournamentMsg.getTournamentID());
							c.sendTCP(tmntResponse);
							ArrayList<JungleClientConnection> clients = tournaments.get(tournamentMsg.getTournamentID()).getPlayerConnections();
							for(JungleClientConnection conn : clients) {
								tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "Tournament has been terminated by the owner, and all the unfinished games will remain playable");
								conn.sendTCP(tmntResponse);
							}
							tournaments.remove(tournamentMsg.getTournamentID());

						}else{
							tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "The tournament ID does not exists");
							c.sendTCP(tmntResponse);
						}
					}
					else if(tournamentMsg.getType() == TournamentMessageType.REPORT) {
						String tmpstr = "";
						for(String s : tournaments.keySet()) {
							if(tournaments.get(s).getRoundNum()==0){
								tmpstr+=s+" by "+tournaments.get(s).getTournamentOwner()+"\n";
							}
						}
						if(!tmpstr.isEmpty()){
							tmpstr = tmpstr.substring(0, tmpstr.length()-1);
						}
						tmntResponse = new TournamentMessage(tournamentMsg.getTournamentID(), TournamentMessageType.RESULT, "List of active tournaments:\n"+tmpstr);
						c.sendTCP(tmntResponse);
					}
				}
				// Game communication received, pass it off to the correct game controller
				if(object instanceof GameMessage) {
					GameMessage message = (GameMessage)object;
					if(games.containsKey(message.getGameID())) {
						games.get(message.getGameID()).handleMessage(message);
					}
					else {
						System.out.println("GameMessage received for game that does not exist on the server");
					}
				}
			}
			public void disconnected (Connection c) {
				JungleClientConnection conn = (JungleClientConnection)c;
				// If the disconnected connection has an associated user, log them out in the database. Otherwise, do nothing, kryo gets rid of the connection
				if(conn.getUser() != null) {
					database.logout(conn.getUser());
					// Store any active games the user was playing before they disconnected
					User user = conn.getUser();
					Set<Integer> gameIDs = database.gameIDs(user.getId());
					for(int id: gameIDs) {
						if(games.containsKey(id)) {
							ServerGameController controller = games.get(id);
							database.updateGame(controller.gameID, controller.getBoardRepresentation(), controller.currentTurn());
						}
					}
				}
			}
		});
		server.bind(Network.port);
		server.start();
	}

	public void updateGameInDB(ServerGameController controller) {
		database.updateGame(controller.gameID, controller.getBoardRepresentation(), controller.currentTurn());
	}

	private void createGame(JungleClientConnection player1, JungleClientConnection player2, String tournamentID){
		ServerGameController newGame = new ServerGameController(gameCounter, player1, player2, this, tournamentID);
		games.put(gameCounter, newGame);
		database.addGame(gameCounter, player1.getUser().getId(), player2.getUser().getId(), new Timestamp(System.currentTimeMillis()), 1, null);
		// Send the game info to the clients
		GameInstance inviterMessage = new GameInstance(gameCounter, player2.getUser(), Color.WHITE, null);
		GameInstance inviteeMessage = new GameInstance(gameCounter, player1.getUser(), Color.BLACK, null);
		player1.sendTCP(inviterMessage);
		player2.sendTCP(inviteeMessage);
		gameCounter++;
		newGame.startGame();
	}

	private void createTournamentGames(String tournamentID) {
		ArrayList<String> matches = tournaments.get(tournamentID).getCurrentPlacement();
		for(String s : matches) {
			JungleClientConnection player1 = null;
			JungleClientConnection player2 = null;
			if(s.contains(",")){
				for(Connection conn : server.getConnections()) {
					if(((JungleClientConnection) conn).getUser() != null && ((JungleClientConnection)conn).getUser().getNickname().equals(s.split(",")[0]))
						player1 = (JungleClientConnection)conn;
					else if(((JungleClientConnection)conn).getUser() != null && ((JungleClientConnection)conn).getUser().getNickname().equals(s.split(",")[1]))
						player2 = (JungleClientConnection)conn;
				}
				if(player1 != null && player2 != null) {
					createGame(player1, player2, tournamentID);
				}
			}
		}
	}

	// Sends all active games the connection has stored in the database
	// Attempts to find a currently running game controller, otherwise creates a new one for the game
	private void sendActiveGames(JungleClientConnection jClient) {
		Set<Integer> gameIDs = database.gameIDs(jClient.getUser().getId());
		for(int id: gameIDs) {
			GameInstance game;
			ServerGameController controller;
			// Game already has a controller
			if(this.games.containsKey(id)) {
				controller = this.games.get(id);
				if(controller.player1User.equals(jClient.getUser()))
					controller.setPlayer1(jClient);
				else
					controller.setPlayer2(jClient);
				game = new GameInstance(controller.gameID, controller.getOpponent(jClient.getUser()), controller.getColor(jClient.getUser()), controller.getBoardRepresentation());
				jClient.sendTCP(game);
				controller.rejoinGame();
			}
			// Else create a new controller for the game
			else {
				gameInfo info = database.findGame(id);
				User player1 = database.findUser(database.searchNickname(info.getUser1())).getUser();
				User player2 = database.findUser(database.searchNickname(info.getUser2())).getUser();
				controller = new ServerGameController(info.getGameID(), player1, player2, info.getGameConfig(), this);
				if(player1.equals(jClient.getUser()))
					controller.setPlayer1(jClient);
				else
					controller.setPlayer2(jClient);
				game = new GameInstance(controller.gameID, controller.getOpponent(jClient.getUser()), controller.getColor(jClient.getUser()), controller.getBoardRepresentation());
				this.games.put(controller.gameID, controller);
				jClient.sendTCP(game);
				if(info.getPlayerTurn() == 1)
					controller.resumeGame(Color.WHITE);
				else
					controller.resumeGame(Color.BLACK);
			}
		}
	}

	public void gameOver(int gameID, User winner, User loser, boolean abandoned, Timestamp start, Timestamp end) {
		if(games.get(gameID).isTournamentGame()) {
			Tournament tmnt = tournaments.get(games.get(gameID).getTournamentID());
			if(tmnt!=null){
				int prevRound = tmnt.getRoundNum();
				tmnt.reportWinner(winner);
				int currentRound = tmnt.getRoundNum();
				if(!tmnt.getWinner().isEmpty()){
					for(JungleClientConnection c : tmnt.getPlayerConnections()) {
						TournamentMessage tmntResponse = new TournamentMessage(tmnt.getTournamentID(), TournamentMessageType.RESULT, "Tournament has finished!\nTournament("+tmnt.getTournamentID()+") Result:\n"+tmnt.getTournamentHistory());
						c.sendTCP(tmntResponse);
					}
					tournaments.remove(tmnt);
				}else {
					if(prevRound!=currentRound){
						for(JungleClientConnection c : tmnt.getPlayerConnections()) {
							TournamentMessage tmntResponse = new TournamentMessage(tmnt.getTournamentID(), TournamentMessageType.RESULT, "New round of Tournament("+tmnt.getTournamentID()+") has begun\n"+tmnt.getTournamentHistory().split("\n")[0]);
							c.sendTCP(tmntResponse);
						}
						createTournamentGames(tmnt.getTournamentID());
					}
				}
			}
		}
		database.addGameRecord(gameID, new GameRecord(winner.getId(), loser.getNickname(), start, end, true, abandoned),
				new GameRecord(loser.getId(), winner.getNickname(), start, end, false, abandoned));
		games.remove(gameID);
	}

	public void stop() {
		server.stop();
	}

	// for testing, may be removed if not needed
	public void send(Connection c, Object object) {
		c.sendTCP(object);
	}

	private boolean validEmail(String email) {
		if (email.contains("@")) {
			return email.substring(Math.max(email.length() - 6, 0)).contains(".");// should be improved
		}
		return false;
	}

	public ServerGameController getController(int gameID) {
		return games.get(gameID);
	}

	public static void main(String args[]) {
		try {
			//pass in port
			if(args.length == 1) {
				new JungleServer();
			}else
			//pass in database information
			if(args.length == 3) {
				new JungleServer(args[0],args[1],args[3]);
			}
			else {
				new JungleServer();
			}
		} catch (IOException e) {
			System.err.println("Exception in server: "+e.getMessage());
		}
	}
}
