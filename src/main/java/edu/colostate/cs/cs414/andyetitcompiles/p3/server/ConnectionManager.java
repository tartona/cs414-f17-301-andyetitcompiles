package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import com.esotericsoftware.kryonet.Connection;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

/**
 * 
 * Used to connect an online user with their server connection. 
 *
 */
public class ConnectionManager {
		private User user;
		private Connection connection;
		
		public ConnectionManager(User user, Connection c) {
			this.user = user;
			this.connection=c;
		}
		
		public void setUser(User user) {
			this.user = user;
		}
		
		public User getUser() {
			return this.user;
		}
		
		public Connection getConnection() {
			return this.connection;
		}

}
