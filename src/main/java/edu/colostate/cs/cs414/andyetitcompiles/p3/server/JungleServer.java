package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.Network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class JungleServer {
	Server kryoServer;
	
	public JungleServer() throws IOException {
		// Initialize the kryonet server
		kryoServer = new Server() {
			protected Connection newConnection() {
				// Use our own implementation of connection to keep track of the user associated with a connection
				return new JungleClientConnection();
			}
		};
		
		// Register the objects that will be used for communication
		Network.register(kryoServer);
		
		// Add a listener for connections
		kryoServer.addListener(new Listener() {
			// Called everytime a new message is received from a client
			public void received(Connection c, Object object) {
				// All of the connections to the server will be JungleClientConnection
				JungleClientConnection connection = (JungleClientConnection)c;
				
				// Add logic to handle the different types of messages
			}
			
			// Called everytime a client disconnects
			public void disconnected(Connection c) {
				
			}
		});
		// bind the server to the port specified in Network class and start
		kryoServer.bind(Network.port);
		kryoServer.start();
	}
	
	public void stopServer() {
		kryoServer.stop();
	}
}
