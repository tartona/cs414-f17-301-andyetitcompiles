package edu.colostate.cs.cs414.andyetitcompiles.kryotest;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class EchoClient {
	Client client;

	public EchoClient() {
		// First initialize the client and start it
		client = new Client();
		client.start();
		
		// This uses the Network class to make sure the server and client have the same classes registered
		Network.register(client);
		
		// Now we can add a listeners for the connection
		client.addListener(new Listener() {
			public void connected(Connection connection) {
				System.out.println("Connected to server");
				Message message = new Message();
				message.content = "Hello Server";
				client.sendTCP(message);
			}
		});
		
		// And then try and connect
		try {
			client.connect(5000, "localhost", Network.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		new EchoClient();
	}
}
