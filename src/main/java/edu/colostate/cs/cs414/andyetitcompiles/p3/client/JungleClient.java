package edu.colostate.cs.cs414.andyetitcompiles.p3.client;
import java.io.IOException;

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
	
	public JungleClient() {
		loggedIn = false;
		kryoClient = new Client();
		initializeKryoClient();
	}
	
	// This contains all the code for setting up the kryo client
	public void initializeKryoClient() {
		kryoClient.start();
		// Register the client with the Network class
		Network.register(kryoClient);
		
		// Define listeners
		kryoClient.addListener(new Listener() {
			// Called after client successfully connects to the server
			public void connected(Connection c) {
				System.out.println("Successfully connected");
			}
			// This listener is where we handle any requests from the server that
			// are unsolicited from the client, such as invite requests from other users.
			// All the other listeners are defined in their respective methods (login, register, unregister, finduser)
			public void received(Connection c, Object o) {
				if(o instanceof InviteRequest) {
					InviteRequest request = (InviteRequest)o;
					handleInviteRequest(request);
				}

			}
			// Called whenever the client is disconnected from the server
			public void disconnected(Connection c) {
				loggedIn = false;
				System.out.println("You have been disconnected from the server");
			}
		});
		new Thread("Connect") {
			public void run () {
				try {
					kryoClient.connect(5000, Network.host, Network.port);
				} catch (IOException ex) {
					System.out.println("Something went wrong while connecting to the server: " + ex.getMessage());
				}
			}
		}.start();
	}

	private void handleInviteRequest(InviteRequest request) {
		System.out.println("You have been invited to play jungle by: " + request.getInviter().getNickname());
		// Now we send a notification to the ui, which will then communicate back whether the user accepts or rejects
		// I can't do this until the interface to the ui is defined.
	}

	// Sends login request to the server
	public void login(String email, String password) {
		if(loggedIn) {
			System.out.println("Already logged in!");
			return;
		}
		// Add a listener to listen for the server response
		Listener loginListener = new Listener () {
			public void received(Connection c, Object o) {
				// Do nothing if the object is not a LoginResponse
				if(o instanceof LoginResponse) {
					handleLoginResponse((LoginResponse)o);
				}
			}
		};
		kryoClient.addListener(loginListener);
		LoginRequest request = new LoginRequest(email, password);
		kryoClient.sendTCP(request);
	}
	
	// Called when a LoginResponse object is received from the server
	private void handleLoginResponse(LoginResponse response) {
		if(response.successful()) {
			loggedIn = true;
			clientUser = response.getUser();
			System.out.println(response.getMessage());
		}
		else {
			System.out.println(response.getMessage());
		}
		
	}

	// Sends registration request to the server
	public void register(String email, String nickname, String password) {
		// Add a listener to listen for the server response
		if(loggedIn) {
			System.out.println("Can't register a new user if you are already logged in!");
			return;
		}
		Listener registerListener = new Listener() {
			public void received(Connection c, Object o) {
				if(o instanceof RegisterResponse) {
					handleRegisterResposne((RegisterResponse)o);
				}
			}
		};
		kryoClient.addListener(registerListener);
		RegisterRequest request = new RegisterRequest(email, nickname, password);
		kryoClient.sendTCP(request);
	}
	
	private void handleRegisterResposne(RegisterResponse response) {
		if(response.successful()) {
			System.out.println(response.getMessage());
		}
		else {
			System.out.println(response.getMessage());
		}
	}

	// Sends unregistration request to the server
	public void unregister(String email, String password) {
		if(!loggedIn) {
			System.out.println("Please login with the account you want to unregister");
			return;
		}
		Listener unregisterListener = new Listener() {
			public void received(Connection c, Object o) {
				if(o instanceof UnregisterResponse) {
					UnregisterResponse response = (UnregisterResponse)o;
					handleUnregisterResponse(response);
				}
			}
		};
		kryoClient.addListener(unregisterListener);
		UnregisterRequest request = new UnregisterRequest(email, password);
		kryoClient.sendTCP(request);
	}
	
	private void handleUnregisterResponse(UnregisterResponse response) {
		if(response.successful()) {
			System.out.println(response.getMessage());
		}
		else {
			System.out.println(response.getMessage());
		}
	}

	// Sends findUser request to the server. 
	public void findUser(String nickname) {
		Listener userListener = new Listener() {
			public void received(Connection c, Object o) {
				if(o instanceof UserResponse) {
					UserResponse response = (UserResponse)o;
					handleUserResponse(response);	
					}
				}
			};
		kryoClient.addListener(userListener);
		UserRequest request = new UserRequest(nickname);
		kryoClient.sendTCP(request);
	}
	
	private void handleUserResponse(UserResponse response) {
		// TODO: figure out how to handle the user object. Probably something with the ui.
		// I was thinking maybe this triggers a popup with the user, and you can choose
		// to invite them or view their profile page, or maybe it just pops up the profile page
	}
	
	// Sends an invite request to the server 
	public void invite(User otherUser) {
		Listener inviteListener = new Listener() {
			public void received(Connection c, Object o) {
				if(o instanceof InviteResponse) {
					InviteResponse response = (InviteResponse)o;
					handleInviteResponse(response);
				}
			}
		};
		kryoClient.addListener(inviteListener);
		InviteRequest request = new InviteRequest(otherUser, clientUser);
		kryoClient.sendTCP(request);
	}
	
	private void handleInviteResponse(InviteResponse response) {
		if(response.isAccepted()) {
			System.out.println(response.getMessage());
		}
		else {
			System.out.println(response.getMessage());
		}
	}

	public boolean getConnectedStatus() {
		return kryoClient.isConnected();
	}
	
	public boolean getLoggedInStatus() {
		return loggedIn;
	}
	
	// Stops the client
	public void stop() {
		kryoClient.stop();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
