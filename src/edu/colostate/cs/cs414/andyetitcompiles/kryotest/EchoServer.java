package edu.colostate.cs.cs414.andyetitcompiles.kryotest;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class EchoServer {
	Server server;
	
	public EchoServer() throws IOException {
		server = new Server();
		
		// Register the server so it uses the same classes as the client
		Network.register(server);
		
		// Add listeners 
		server.addListener(new Listener() {
			public void received(Connection c, Object object) {
				if(object instanceof Message) {
					Message message = (Message)object;
					System.out.println(message.content);
				}
			}
		});
		// Bind to a port and start the server
		server.bind(Network.port);
		server.start();
	}
	
	public static void main(String[] args) throws IOException {
		new EchoServer();
	}
	
}