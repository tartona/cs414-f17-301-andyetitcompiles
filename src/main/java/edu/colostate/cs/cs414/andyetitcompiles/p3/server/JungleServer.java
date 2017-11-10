package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.io.IOException;
import java.sql.SQLException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleServer {
	Server server;
	DatabaseManager database;
	ServerGameController gameController;

	public JungleServer(DatabaseManager database) throws IOException {
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
			this.database = new DatabaseManagerSets();
		}
		networkSetup();
	}

	private void networkSetup() throws IOException {
		Network.register(server);

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
				// IMPORTANT TODO: the server can only handle 1 game right now for the demo, so don't try and create more than one game
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
							gameController = new ServerGameController(1, inviter, invitee);
							// Send the game info to the clients
							GameInstance inviterMessage = new GameInstance(1, invitee.getUser(), Color.WHITE);
							GameInstance inviteeMessage = new GameInstance(1, inviter.getUser(), Color.BLACK);
							inviter.sendTCP(inviterMessage);
							invitee.sendTCP(inviteeMessage);
							gameController.startGame();
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
				// Game communication received, pass it off to the game controller
				// IMPORTANT TODO: again, this assumes only one game will be created. We will need to check the game id and pass it to the correct controller in the future
				if(object instanceof GameMessage) {
					gameController.handleMessage((GameMessage)object);
				}
			}
			public void disconnected (Connection c) {
				JungleClientConnection conn = (JungleClientConnection)c;
				// If the disconnected connection has an associated user, log them out in the database. Otherwise, do nothing, kryo gets rid of the connection
				if(conn.getUser() != null) {
					database.logout(conn.getUser());
				}
			}
		});
		server.bind(Network.port);
		server.start();
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

	// for testing
	public ServerGameController getController() {
		return gameController;
	}
	
	
	public static void main(String args[]) {
		try {
			new JungleServer();
		} catch (IOException e) {
			System.out.println("Exception in server: "+e.getMessage());
		}
	}
}
