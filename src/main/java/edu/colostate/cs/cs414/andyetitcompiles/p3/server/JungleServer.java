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
	
	public JungleServer(DatabaseManager database) {
		server = new Server();
		this.database = database;
		networkSetup();
	}
	public JungleServer() {
		server = new Server();
		this.database = new DatabaseManager();
		networkSetup();
	}
	
	private void networkSetup() {
		Network.register(server);
		
		server.addListener(new Listener() {
			public void received(Connection c, Object object) {
				
				if(object instanceof LoginRequest) {
					LoginRequest loginRequest = (LoginRequest)object;
					//TODO login, access database, respond to user
					boolean isSuccessful = false;
					User user = null;
					String message = null;
					c.sendTCP(new LoginResponse(isSuccessful, user, message));
				}
				if(object instanceof RegisterRequest) {
					lastReceived = object;
					RegisterRequest registerRequest = (RegisterRequest)object;
					User user = new User(registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getNickname());
					boolean isSuccessful = database.registerUser(user);
					//TODO register, check database for user/email, add to database, respond to user
					String message = ("User registration " + isSuccessful);
					c.sendTCP(new RegisterResponse(isSuccessful, message));
				}
				if(object instanceof UnregisterRequest) {
					lastReceived = object;
					UnregisterRequest unregisterRequest = (UnregisterRequest)object;
					//TODO unregister, check database for user/email, remove from database, respond to user
					boolean isSuccessful = false;
					String message = null;
					c.sendTCP(new UnregisterResponse(isSuccessful, message));
				}
				if(object instanceof UserRequest) {
					lastReceived = object;
					UserRequest userRequest = (UserRequest)object;
					boolean isSuccessful = false;
					User user = null;
					String message = null;
					c.sendTCP(new UserResponse(isSuccessful, user, message));
				}
				// Sending invites from the client does not block to wait for a response, so just update the object
				if(object instanceof InviteRequest) {
					lastReceived = object;
					
					boolean isAccepted = false;
					User inviter = null;
					User invitee = null;
					String message = null;
					c.sendTCP(new InviteResponse(isAccepted, inviter, invitee, message));
					
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
	
	//TODO improve email check
	private boolean validEmail(String email) {
		if(email.contains("@")) {
			return email.substring(Math.max(email.length()-6,0)).contains(".");//should be improved
		}
		
		return false;
		
	}
}
