package edu.colostate.cs.cs414.andyetitcompiles.p3;
import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class JungleClient {
	Client kryoClient;
	boolean loggedIn;
	User clientUser;
	String host;
	
	public JungleClient() {
		// Initialize the kryo client
		kryoClient = new Client();
		
		// Register the client with the Network class
		Network.register(kryoClient);
		
		// Add listeners
		kryoClient.addListener(new Listener() {
			// Called after client successfully connects to the server
			public void connected(Connection c) {
				
			}
			// Called whenever the client receives a message from the server
			public void received(Connection c, Object object) {
				
			}
			// Called whenever the client is disconnected from the server
			public void disconnected(Connection c) {
				
			}
		});
		
		// Start a new thread to connect to the server so the ui is still responsive while connecting
		new Thread("Connect") {
			public void run() {
				try {
					// Attempt to connect to the server. Host is hardcoded in JungleClient, but maybe later
					// on it could be user input. The port is defined in the Network class. 5000ms timeout
					kryoClient.connect(5000, host, Network.port);
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
	public void unregister() {
		
	}
	
	// Sends findUser request to the server. Returns a User object
	public User findUser(String nickname) {
		return null;
	}
	
	// Sends an invite request to the server 
	public void invite(User otherUser) {
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
