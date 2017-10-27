package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.util.Set;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.RegisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UnregisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UserResponse;

import java.util.HashSet;
import java.util.Iterator;

public class DatabaseManager {
	private Set<User> registeredUsers;
	private Set<User> onlineUsers;
	// TODO add real database

	public DatabaseManager() {
		registeredUsers = new HashSet<User>();
		onlineUsers = new HashSet<User>();

	}

	public RegisterResponse registerUser(User user) {
		for (User userItr : registeredUsers) {
			if (userItr.getEmail().equalsIgnoreCase(user.getEmail()) || userItr.getNickname().equalsIgnoreCase(user.getNickname())) {
				return new RegisterResponse(false, "User already exists");
			}
		}
		registeredUsers.add(user);
		System.out.println("Database: " + registeredUsers.size());
		return new RegisterResponse(true, "User Registered");
	}

	public RegisterResponse registerUser(String email, String password, String nickname) {
		return registerUser(new User(email, password, nickname));
	}

	public UnregisterResponse unRegisterUser(String email, String password) {
		for (User user : registeredUsers) {
			if (user.getEmail().equalsIgnoreCase(email) || user.getNickname().equalsIgnoreCase(email)) {
				if (user.getPassword().equals(password)) {
					return new UnregisterResponse(true, "User unregisteration Sucessful");
				} else { // user found password incorrect
					return new UnregisterResponse(false, "Incorrect Password");
				}
			}
		}
		return new UnregisterResponse(false, "User not found");
	}

	// TODO add javadoc
	public LoginResponse authenticateUser(String email, String password) {
		for (User user : registeredUsers) {
			if (user.getEmail().equalsIgnoreCase(email) || user.getNickname().equalsIgnoreCase(email)) {
				if (user.getPassword().equals(password)) {
					onlineUsers.add(user);
					return new LoginResponse(true, user, "Login Sucessful");
				} else { // user found password incorrect
					return new LoginResponse(false, null, "Incorrect Password: expected:" + user.getPassword() + " received: " +password);
				}
			}
		}
		return new LoginResponse(false, null, "User not found");
	}

	public void logout(User user) {
		onlineUsers.remove(user);
	}

	// take username or email as input for user.
	public UserResponse findUser(String username) {
		for (User user : onlineUsers) {
			if (user.getEmail().equalsIgnoreCase(username) || user.getNickname().equalsIgnoreCase(username)) {
				return new UserResponse(true, user, "User Found");
			}
		} 
		return new UserResponse(false, null, "User not found");
	}
}