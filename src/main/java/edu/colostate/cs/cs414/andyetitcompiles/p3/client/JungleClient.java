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
				System.out.println("Client: Successfully connected");
			}
			// Called whenever the client receives a message from the server
			public void received(Connection c, Object object) {
				
			}
			// Called whenever the client is disconnected from the server
			public void disconnected(Connection c) {
				loggedIn = false;
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
	// Sends login request to the server and defines the listener used for login responses
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
					LoginResponse response = (LoginResponse)o;
					// Login successful
					if(response.successful()) {
						clientUser = response.getUser();
						loggedIn = true;
						// This is where we would add some sort of UI update, just print to the console for now
						System.out.println(response.getMessage());
					}
					else {
						System.out.println(response.getMessage());
					}
				}
			}
		};
		kryoClient.addListener(loginListener);
		// Send the request
		LoginRequest request = new LoginRequest(email, password);
		kryoClient.sendTCP(request);
	}
	
	// Sends registration request to the server
	public void register(String email, String nickname, String password) {
		// Add a listener to listen for the server response
		Listener registerListener = new Listener() {
			public void received(Connection c, Object o) {
				if(o instanceof RegisterResponse) {
					RegisterResponse response = (RegisterResponse)o;
					if(response.successful()) {
						System.out.println(response.getMessage());
					}
					else {
						System.out.println(response.getMessage());
					}
				}
			}
		};
		kryoClient.addListener(registerListener);
		RegisterRequest request = new RegisterRequest(email, nickname, password);
		kryoClient.sendTCP(request);
	}
	
	// Sends unregistration request to the server
	public void unregister(String email, String password) {
		Listener unregisterListener = new Listener() {
			public void received(Connection c, Object o) {
				if(o instanceof UnregisterResponse) {
					UnregisterResponse response = (UnregisterResponse)o;
					if(response.successful()) {
						// If it was successful, the server should kick you, so no need to disconnect
						System.out.println(response.getMessage());
					}
					else {
						System.out.println(response.getMessage());
					}
				}
			}
		};
		kryoClient.addListener(unregisterListener);
		UnregisterRequest request = new UnregisterRequest(email, password);
		kryoClient.sendTCP(request);
	}
	
	// Sends findUser request to the server. Returns a User object
	public User findUser(String nickname) {
		return null;
	}
	
	// Sends an invite request to the server 
	public void invite(User otherUser) {
		
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
