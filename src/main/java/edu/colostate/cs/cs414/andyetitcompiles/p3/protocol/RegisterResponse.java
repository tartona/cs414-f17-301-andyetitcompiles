package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

// RegisterResponses are sent by the server to any client that has sent a RegisterRequest
public class RegisterResponse {
	boolean isSuccessful;
	String message;

	public RegisterResponse(boolean isSuccesful, String message) {
		this.isSuccessful = true;
		this.message = message;
	}
	
	public RegisterResponse() {}
<<<<<<< HEAD
	public boolean successful() {return isSuccessful;}
	public String getMessage() {return message;}
}
=======

	public boolean successful() {return isSuccessful;}
	
	public String getMessage() {return message;}

}
>>>>>>> branch 'master' of https://github.com/tartona/cs414-f17-301-andyetitcompiles
