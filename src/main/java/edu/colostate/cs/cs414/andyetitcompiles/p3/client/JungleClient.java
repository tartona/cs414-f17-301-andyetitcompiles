package edu.colostate.cs.cs414.andyetitcompiles.p3.client;
import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.Network;

public class JungleClient {
	Client kryoClient;
	boolean loggedIn;
	boolean connected;
	User clientUser;
	String host;
	
	// Regular production constructor
	public JungleClient() {
		// Initialize the kryo client
		kryoClient = new Client();
		initializeKryoClient();
	}
	
	// This constructor is only used by JungleClientTest so a mock kryo client can be used
	public JungleClient(Client client) {
		kryoClient = client;
		initializeKryoClient();
	}
	
	// This contains all the code for setting up the kryo client
	public void initializeKryoClient() {
		// Register the client with the Network class
		Network.register(kryoClient);
		
		// Define listeners
		kryoClient.addListener(new Listener() {
			// Called after client successfully connects to the server
			public void connected(Connection c) {
				connected = true;
			}
			// Called whenever the client receives a message from the server
			public void received(Connection c, Object object) {
				
			}
			// Called whenever the client is disconnected from the server
			public void disconnected(Connection c) {
				loggedIn = false;
				connected = false;
			}
		});
		
		// Start a new thread to connect to the server so the ui is still responsive while connecting
		new Thread("Connect") {
			public void run() {
				try {
					// Attempt to connect to the server. The port and host is defined in the Network class. 5000ms timeout
					kryoClient.connect(5000, Network.host, Network.port);
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}
	// Sends login request to the server
	public void login(String email, String password) {
		
	}
	
	// Sends registration request to the server
	public void register(String email, String nickname, String password) {
		
	}
	
	// Sends unregistration request to the server
	public void unregister(String email, String password) {
		
	}
	
	// Sends findUser request to the server. Returns a User object
	public User findUser(String nickname) {
		return null;
	}
	
	// Sends an invite request to the server 
	public void invite(User otherUser) {
		
	}
	
	public boolean getConnectedStatus() {
		return connected;
	}
	
	public boolean getLoggedInStatus() {
		return loggedIn;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
