package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleServer {
	Server server;
	DatabaseManager database;
	Set<ConnectionManager> onlineUsers;

	public JungleServer(DatabaseManager database) throws IOException {
		server = new Server();
		this.database = database;
		onlineUsers = new HashSet<ConnectionManager>();
		networkSetup();
	}

	public JungleServer() throws IOException {
		server = new Server();
		this.database = new DatabaseManager();
		onlineUsers = new HashSet<ConnectionManager>();
		networkSetup();
	}

	private void networkSetup() throws IOException {
		Network.register(server);

		server.addListener(new Listener() {
			public void received(Connection c, Object object) {

				if (object instanceof LoginRequest) {
					LoginRequest loginRequest = (LoginRequest) object;
					LoginResponse loginResp = database.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
					c.sendTCP(loginResp);
					if(loginResp.successful()) {
						c.setName(loginResp.getUser().getNickname());//set connection name to user nickname
						onlineUsers.add(new ConnectionManager(loginResp.getUser(), c));
					}
				}
				if (object instanceof RegisterRequest) {
					RegisterRequest registerRequest = (RegisterRequest) object;
					User user = new User(registerRequest.getEmail(), registerRequest.getNickname(),
							registerRequest.getPassword());
					if (validEmail(user.getEmail())) {
						System.out.println("Sending RegisterResponse");
						c.sendTCP(database.registerUser(user));
					} else {
						c.sendTCP(new RegisterResponse(false, "Invalid Email Address"));
					}
				}
				if (object instanceof UnregisterRequest) {
					UnregisterRequest unregisterRequest = (UnregisterRequest) object;
					c.sendTCP(database.unRegisterUser(unregisterRequest.getEmail(), unregisterRequest.getPassword()));
				}
				if (object instanceof UserRequest) {
					UserRequest userRequest = (UserRequest) object;
					c.sendTCP(database.findUser(userRequest.getNickname()));
				}
				// Sending invites from the client does not block to wait for a response, so
				// just update the object
				if (object instanceof InviteRequest) {
					InviteRequest inviteReq = (InviteRequest)object;

					for (ConnectionManager clientConn : onlineUsers) {
						//find online user with username
						if (clientConn.getUser().getNickname().equalsIgnoreCase(inviteReq.getInvitee().getNickname())) {
							clientConn.getConnection().sendTCP(inviteReq);//invite user
						}
					}
					
					boolean isAccepted = false;
					User inviter = null;
					User invitee = null;
					String message = null;
					c.sendTCP(new InviteResponse(isAccepted, inviter, invitee, message));

				}
			}
			public void disconnected (Connection c) {
				String nickname = c.toString();
				for (ConnectionManager clientConn : onlineUsers) {
					if (clientConn.getUser().getNickname().equalsIgnoreCase(nickname)) {
						onlineUsers.remove(clientConn);
					}
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
