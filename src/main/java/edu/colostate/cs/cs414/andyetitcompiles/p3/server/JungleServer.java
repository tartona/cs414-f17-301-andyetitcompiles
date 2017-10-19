package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleServer {
	Server server;
	Object lastReceived;
	DatabaseManager database;
	
	public JungleServer() {
		server = new Server();
		database = new DatabaseManager();
		
		Network.register(server);
		
		server.addListener(new Listener() {
			public void received(Connection c, Object object) {
				
				if(object instanceof LoginRequest) {
					lastReceived = object;
					LoginRequest loginRequest = (LoginRequest)object;
					//TODO login, access database, respond to user
				}
				if(object instanceof RegisterRequest) {
					lastReceived = object;
					RegisterRequest registerRequest = (RegisterRequest)object;
					//TODO register, check database for user/email, add to database, respond to user
				}
				if(object instanceof UnregisterRequest) {
					lastReceived = object;
					UnregisterRequest unregisterRequest = (UnregisterRequest)object;
					//TODO unregister, check database for user/email, remove from database, respond to user
				}
				if(object instanceof UserRequest) {
					lastReceived = object;
					UserRequest userRequest = (UserRequest)object;
				}
				// Sending invites from the client does not block to wait for a response, so just update the object
				if(object instanceof InviteRequest) {
					lastReceived = object;
				}
			}
		});
		
	}
	
	public void stop() {
		server.stop();
	}
	
	//for testing, may be removed if not needed
	public void send(Connection c, Object object) {
		c.sendTCP(object);
	}
	
	public Object getLastReceived() {
		return lastReceived;
	}
}
