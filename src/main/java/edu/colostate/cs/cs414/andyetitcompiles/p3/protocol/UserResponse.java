package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

// UserResponses are sent by the server to any user that sent a UserRequest
public class UserResponse {
	boolean isSuccessful;
	User user;
	String message;

	public UserResponse(boolean isSuccessful, User user, String message) {
		this.isSuccessful = isSuccessful;
		this.user = user;
		this.message = message;
	}
	
<<<<<<< HEAD
	public boolean isSuccesful() {return isSuccesful;}
	public User getUser() {return user;}
	public String getMessage() {return message;}
}
=======
	public UserResponse() {}

	public User getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}

	public boolean successful() {
		// TODO Auto-generated method stub
		return isSuccessful;
	}
	
}
>>>>>>> branch 'master' of https://github.com/tartona/cs414-f17-301-andyetitcompiles
