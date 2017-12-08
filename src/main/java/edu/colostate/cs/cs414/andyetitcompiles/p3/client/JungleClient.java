package edu.colostate.cs.cs414.andyetitcompiles.p3.client;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleClient {
	Client kryoClient;
	boolean loggedIn;
	User clientUser;
	String host;
	User requestedUser;
	// Collection of active games the client is playing
	Map<Integer, ClientGameController> activeGames;
	// Queue for incoming messages from the ui
	BlockingQueue<String> inQueue;
	// Queue for outgoing messages to the ui
	BlockingQueue<String> outQueue;
	// The ui class
	private JungleCLI jungleUI;

	public JungleClient(String host, int port) {
		loggedIn = false;
		kryoClient = new Client(8192, 4096);
		activeGames = new HashMap<Integer, ClientGameController>();
		inQueue = new LinkedBlockingQueue<String>();
		outQueue = new LinkedBlockingQueue<String>();
		initializeKryoClient(host, port);
		// I am implementing the CLI interface on its own thread, and the ideas used there could probably be extended to the actual ui
		jungleUI = new JungleCLI(this, outQueue, inQueue);
		Thread UI = new Thread(jungleUI);
		UI.start();
	}

	public JungleClient() {
		loggedIn = false;
		kryoClient = new Client(8192, 4096);
		activeGames = new HashMap<Integer, ClientGameController>();
		inQueue = new LinkedBlockingQueue<String>();
		outQueue = new LinkedBlockingQueue<String>();
		initializeKryoClient(Network.host, Network.port);
		// I am implementing the CLI interface on its own thread, and the ideas used there could probably be extended to the actual ui
		jungleUI = new JungleCLI(this, outQueue, inQueue);
		Thread UI = new Thread(jungleUI);
		UI.start();
	}

	public static void main(String[] args) {
		new JungleClient(args[0], Integer.parseInt(args[1]));
	}

	private void pushUpdate(String message) {
		try {
			outQueue.put(message);
		} catch (InterruptedException e) {
			e.getMessage();
		}
	}
	private String takeUpdate() {
		try {
			return inQueue.take();
		} catch(InterruptedException e) {
			e.getMessage();
			return null;
		}
	}

	// This contains all the code for setting up the kryo client
	public void initializeKryoClient(String host, int port) {
		kryoClient.start();
		// Register the client with the Network class
		Network.register(kryoClient);

		// Define listeners that run on the main update thread
		kryoClient.addListener(new Listener() {
			// Called after client successfully connects to the server
			public void connected(Connection c) {
				handleConnection();
			}
			// Listeners for server requests and responses
			public void received(Connection c, Object o) {
				// An invite from another user
				if(o instanceof InviteRequest) {
					handleInviteRequest((InviteRequest)o);
				}
				// Response from server after attempting to log in
				if(o instanceof LoginResponse) {
					handleLoginResponse((LoginResponse)o);
				}
				// Response from server after attempting to register
				if(o instanceof RegisterResponse) {
					handleRegisterResposne((RegisterResponse)o);
				}
				// Response from the server after attempting to unregister
				if(o instanceof UnregisterResponse) {
					handleUnregisterResponse((UnregisterResponse)o);
				}
				// Response from the server after attempting to find a user
				if(o instanceof UserResponse) {
					handleUserResponse((UserResponse)o);
				}
				// Response from the server after an invite has been accepted
				if(o instanceof InviteResponse) {
					handleInviteResponse((InviteResponse)o);
				}
				// Sent from the server response of TournamentMessage
				if(o instanceof TournamentMessage) {
					handleTournamentMessage((TournamentMessage)o);
				}
				// Sent by the server after it creates a new game for the client
				if(o instanceof GameInstance) {
					handleGameInstance((GameInstance)o);
				}
				// Sent by the server for game communications
				if(o instanceof GameMessage) {
					handleGameMessage((GameMessage)o);
				}

				if(o instanceof GameInstance[]) {
					GameInstance games[] = (GameInstance[])o;
					for(GameInstance game: games) {
						handleGameInstance(game);
					}
				}
			}
			// Called whenever the client is disconnected from the server
			public void disconnected(Connection c) {
				loggedIn = false;
				pushUpdate("Disconnected");
			}
		});

		// It is best to attempt the connection on its own thread so it does block the loading of other client components (like ui)
		new Thread("Connect") {
			public void run () {
				try {
					kryoClient.connect(5000, host, port);
				} catch (IOException ex) {
					System.out.println("Something went wrong while connecting to the server: " + ex.getMessage());
				}
			}
		}.start();
	}

	// Called when the client first connects to the server
	private void handleConnection() {
		pushUpdate("Connected");
	}

	// Called when the client receives a new game instance from the server
	private void handleGameInstance(GameInstance game) {
		// Create a new game controller
		pushUpdate("Game with " + game.getOpponent().getNickname() + " is starting");
		ClientGameController newGame = new ClientGameController(game.getGameID(), clientUser, game.getOpponent(), game.getColor(), game.getBoard(), this);
		activeGames.put(game.getGameID(), newGame);
		jungleUI.addGame(newGame);
	}

	// Called by the UI when a game is over, removes the game instance from the client
	public void removeGame(int gameID) {
		System.out.println("Removing game " + gameID);
		jungleUI.removeGame(activeGames.get(gameID));
		activeGames.remove(gameID);
	}

	// Called when the client receives a message for an existing game
	private void handleGameMessage(GameMessage message) {
		int id = message.getGameID();
		if(activeGames.containsKey(id)) {
			activeGames.get(id).handleMessage(message);
		}
		else {
			System.out.println("GameMessage received for game that is not in the list of active games for this client");
		}
	}

	// Called when the client receives an invite request from another player
	private void handleInviteRequest(InviteRequest request) {
		pushUpdate("New invite:" + request.getInviter().getNickname());
		// Start a new thread to wait for a response from the ui
		new Thread(() -> {
			String answer = takeUpdate();
			if(answer.equals("Accept"))
				kryoClient.sendTCP(new InviteResponse(true, request.getInviter(), request.getInvitee(), getClientUser().getNickname() + " has accepted your invite."));
			else
				kryoClient.sendTCP(new InviteResponse(false, request.getInviter(), request.getInvitee(), getClientUser().getNickname() + " has rejected your invite"));

		}).start();
	}

	// Sends login request to the server
	public void login(String email, String password) {
		if(loggedIn) {
			System.out.println("Already logged in!");
			return;
		}
		LoginRequest request = new LoginRequest(email, password);
		kryoClient.sendTCP(request);
	}

	// Called when the client receives a response form the server after trying to log in
	private void handleLoginResponse(LoginResponse response) {
		if(response.successful()) {
			loggedIn = true;
			clientUser = response.getUser();
			pushUpdate("Login successful:"+clientUser.getNickname());
		}
		else {
			pushUpdate(response.getMessage());
		}

	}

	// Sends registration request to the server
	public void register(String email, String nickname, String password) {
		if(loggedIn) {
			System.out.println("Can't register a new user if you are already logged in!");
			return;
		}
		RegisterRequest request = new RegisterRequest(email, nickname, password);
		kryoClient.sendTCP(request);
	}

	// Called when the client receives a response from the server after trying to register a new user
	private void handleRegisterResposne(RegisterResponse response) {
		if(response.successful()) {
			pushUpdate("Registration successful");
		}
		else {
			pushUpdate(response.getMessage());
		}
	}

	// Sends unregistration request to the server
	public void unregister(String email, String password) {
		if(!loggedIn) {
			System.out.println("Please login with the account you want to unregister");
			return;
		}
		UnregisterRequest request = new UnregisterRequest(email, password);
		kryoClient.sendTCP(request);
	}

	// Called when the client receives a response from the server after trying to unregister
	private void handleUnregisterResponse(UnregisterResponse response) {
		if(response.successful()) {
			pushUpdate("Unregistration Successful");
			clientUser = null;
			loggedIn = false;
		}
		else {
			pushUpdate(response.getMessage());
		}
	}

	// Sends findUser request to the server.
	public void findUser(String nickname) {
		if(!loggedIn) {
			System.out.println("Please login before searching for other users");
			return;
		}
		UserRequest request = new UserRequest(nickname);
		kryoClient.sendTCP(request);
	}

	// Called when the client receives a result from their user query
	private void handleUserResponse(UserResponse response) {
		if(response.successful()) {
			pushUpdate("User found");
			// Set the most recently requested user
			requestedUser = response.getUser();
		}
		else {
			pushUpdate("User not found:" + response.getMessage());
		}
	}

	// Sends an invite request to the server
	public void invite(User invitee) {
		if(!loggedIn) {
			System.out.println("Please login before inviting another user");
			return;
		}
			InviteRequest request = new InviteRequest(invitee, clientUser);
			kryoClient.sendTCP(request);
	}


	// Called when the client receives a reply to an invite they sent
	private void handleInviteResponse(InviteResponse response) {
		if(response.isAccepted()) {
			pushUpdate("Invite accepted:" + response.getInvitee().getNickname());
		}
		else {
			pushUpdate("Invite rejected:" + response.getInvitee().getNickname());
		}
	}

	public void tournamentRequest(String tournamentID, TournamentMessageType type, int maxPlayer) {
		if(!loggedIn) {
			System.out.println("Please login before joining the tournament");
			return;
		}
		else {
			if(type == TournamentMessageType.CREATE) {
				TournamentMessage tournamentRequest = new TournamentMessage(tournamentID, TournamentMessageType.CREATE, clientUser.getNickname(), maxPlayer);
				kryoClient.sendTCP(tournamentRequest);
			}
			else if(type == TournamentMessageType.JOIN) {
				TournamentMessage tournamentRequest = new TournamentMessage(tournamentID, TournamentMessageType.JOIN, "", clientUser.getNickname());
				kryoClient.sendTCP(tournamentRequest);
			}
			else if(type == TournamentMessageType.LEAVE) {
				TournamentMessage tournamentRequest = new TournamentMessage(tournamentID, TournamentMessageType.LEAVE, "", clientUser.getNickname());
				kryoClient.sendTCP(tournamentRequest);
			}
			else if(type == TournamentMessageType.START) {
				TournamentMessage tournamentRequest = new TournamentMessage(tournamentID, TournamentMessageType.START, clientUser.getNickname(), 0);
				kryoClient.sendTCP(tournamentRequest);
			}
			else if(type == TournamentMessageType.END) {
				TournamentMessage tournamentRequest = new TournamentMessage(tournamentID, TournamentMessageType.END, clientUser.getNickname(), 0);
				kryoClient.sendTCP(tournamentRequest);
			}
			else if(type == TournamentMessageType.REPORT) {
				TournamentMessage tournamentRequest = new TournamentMessage(tournamentID, TournamentMessageType.REPORT, clientUser.getNickname(), 0);
				kryoClient.sendTCP(tournamentRequest);
			}
		}
	}

	private void handleTournamentMessage(TournamentMessage response) {
		if(response.getType() == TournamentMessageType.RESULT) {
			pushUpdate(response.getResult());
		}
	}

	public boolean getConnectedStatus() {
		return kryoClient.isConnected();
	}

	public boolean getLoggedInStatus() {
		return loggedIn;
	}

	public User getClientUser() {
		return clientUser;
	}

	public User getRequestedUser() {
		return requestedUser;
	}

	// Stops the client
	public void stop() {
		kryoClient.stop();
	}

	// For testing.
	public ClientGameController getController(int gameID) {
		return activeGames.get(gameID);
	}


}
