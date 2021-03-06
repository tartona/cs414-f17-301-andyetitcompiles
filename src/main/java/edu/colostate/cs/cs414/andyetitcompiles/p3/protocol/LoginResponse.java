package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

// LoginResponses are sent by the server to any client that has sent a LoginRequest
public class LoginResponse {
	boolean isSuccessful;
	User user;
	String message;
	
	public LoginResponse(boolean isSuccessful, User user, String message) {
		this.isSuccessful = isSuccessful;
		this.user = user;
		this.message = message;
	}
	
	public LoginResponse() {}

	public boolean successful() {return isSuccessful;}
	public User getUser() {return user;}
	public String getMessage() {return message;}
}
