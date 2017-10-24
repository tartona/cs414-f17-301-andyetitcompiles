package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleServer {
	Server server;
	Object lastReceived;
	DatabaseManager database;

	public JungleServer(DatabaseManager database) throws IOException {
		server = new Server();
		this.database = database;
		networkSetup();
	}

	public JungleServer() throws IOException {
		server = new Server();
		this.database = new DatabaseManager();
		networkSetup();
	}

	private void networkSetup() throws IOException {
		Network.register(server);

		server.addListener(new Listener() {
			public void received(Connection c, Object object) {

				if (object instanceof LoginRequest) {
					LoginRequest loginRequest = (LoginRequest) object;
					c.sendTCP(database.authenticateUser(loginRequest.getEmail(), loginRequest.getEmail()));
				}
				if (object instanceof RegisterRequest) {
					lastReceived = object;
					RegisterRequest registerRequest = (RegisterRequest) object;
					User user = new User(registerRequest.getEmail(), registerRequest.getPassword(),
							registerRequest.getNickname());
					if (validEmail(user.getEmail())) {
						System.out.println("Sending RegisterResponse");
						c.sendTCP(database.registerUser(user));
					} else {
						c.sendTCP(new RegisterResponse(false, "Invalid Email Address"));
					}
				}
				if (object instanceof UnregisterRequest) {
					lastReceived = object;
					UnregisterRequest unregisterRequest = (UnregisterRequest) object;
					c.sendTCP(database.unRegisterUser(unregisterRequest.getEmail(), unregisterRequest.getPassword()));
				}
				if (object instanceof UserRequest) {
					lastReceived = object;
					UserRequest userRequest = (UserRequest) object;
					c.sendTCP(database.findUser(userRequest.getNickname()));
				}
				// Sending invites from the client does not block to wait for a response, so
				// just update the object
				if (object instanceof InviteRequest) {
					lastReceived = object;

					boolean isAccepted = false;
					User inviter = null;
					User invitee = null;
					String message = null;
					c.sendTCP(new InviteResponse(isAccepted, inviter, invitee, message));

				}
			}
		});
		server.bind(Network.port);
		server.start();
	}

	public void stop() {
		server.stop();
	}

	// for testing, may be removed if not needed
	public void send(Connection c, Object object) {
		c.sendTCP(object);
	}

	public Object getLastReceived() {
		return lastReceived;
	}

	// TODO improve email check
	private boolean validEmail(String email) {
		if (email.contains("@")) {
			return email.substring(Math.max(email.length() - 6, 0)).contains(".");// should be improved
		}

		return false;

	}

	/**
	 * Resets everything in current database for testing.
	 */
	public void resetDatabase() {
		database = new DatabaseManager();
	}
}
