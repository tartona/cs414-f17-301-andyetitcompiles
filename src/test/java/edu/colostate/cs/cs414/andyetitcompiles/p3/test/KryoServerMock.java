package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

// This is a very simple dummy server that sends back a set of static responses based on what the client sends.
// This allows the ability to actually "unit" test the client, without involving the actual server. 
// It can also send arbitrary objects to the client 
// to make it very easy to create new tests, and to test how the client responds to getting responses/requests from
// the server that are unsolicited. Obviously, it will be wise to get some sort of integration test up and running to 
// see if the client and server interact correctly in production, but that should be separate from the unit tests.
public class KryoServerMock {
	Server kryoServer;
	Object lastReceived;
	
	public KryoServerMock() throws IOException {
		kryoServer = new Server();
		
		Network.register(kryoServer);
		
		kryoServer.addListener(new Listener() {
			public void received(Connection c, Object object) {
				// Most of the methods in the client block to wait for a response from the server, so 
				// the mock server has to send back some stock responses so we can test how the client reacts
				if(object instanceof LoginRequest) {
					lastReceived = object;
					LoginRequest actualLogin = (LoginRequest)object;
					if(actualLogin.getEmail().equals("email"))
						send(c, new LoginResponse(true, new User("email", "nickname", "password"), "Login Successful"));
					else
						send(c, new LoginResponse(false, null, "Login Unsuccessful"));
				}
				if(object instanceof RegisterRequest) {
					lastReceived = object;
					RegisterRequest actualRequest = (RegisterRequest)object;
					RegisterRequest correctRequest = new RegisterRequest("email", "nickname", "password");
					if(actualRequest.equals(correctRequest))
						send(c, new RegisterResponse(true, "Registration Successful"));
					else
						send(c, new RegisterResponse(false, "Registration Unsuccessful"));
				}
				if(object instanceof UserRequest) {
					lastReceived = object;
					UserRequest actualRequest = (UserRequest)object;
					UserRequest correctRequest = new UserRequest("nickname");
					if(actualRequest.equals(correctRequest))
						send(c, new UserResponse(false, new User("email", "nickname", "password"), "User found"));
					else
						send(c, new UserResponse(false, null, "User not found"));
				}
				if(object instanceof UnregisterRequest) {
					lastReceived = object;
					UnregisterRequest actual = (UnregisterRequest)object;
					if(actual.getEmail().equals("email")) {
						send(c, new UnregisterResponse(true, "Unregistration Successful"));
					}
					else {
						send(c, new UnregisterResponse(false, "Unregistration Unsuccessful"));
					}
				}
				// Sending invites from the client does not block to wait for a response, so just update the object
				if(object instanceof InviteRequest) {
					lastReceived = object;
				}
			}
		});
		kryoServer.bind(Network.port);
		kryoServer.start();
	}
	
	public void stop() {
		kryoServer.stop();
	}
	
	public void send(Connection c, Object object) {
		c.sendTCP(object);
	}
	
	public Object getLastReceived() {
		return lastReceived;
	}
}
