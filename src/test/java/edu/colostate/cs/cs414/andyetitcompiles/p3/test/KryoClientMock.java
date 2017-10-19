package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.Network;

// This is a mock class so KryoClientMock can be unit tested without depending on the server, or kryonet
public class KryoClientMock {
		Client kryoClient;
		boolean loggedIn;
		boolean connected;
		User clientUser;
		String host;
		
		// Regular production constructor
		public KryoClientMock() {
			// Initialize the kryo client
			kryoClient = new Client();
			initializeKryoClient();
		}
		
		// This constructor is only used by KryoClientMockTest so a mock kryo client can be used
		public KryoClientMock(Client client) {
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
	public void send(Object object) {
		kryoClient.sendTCP(object);
	}	
}
