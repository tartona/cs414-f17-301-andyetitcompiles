package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

// RegisterResponses are sent by the server to any client that has sent a RegisterRequest
public class RegisterResponse {
	boolean isSuccesful;
	String message;

	public RegisterResponse(boolean isSuccesful, String message) {
		this.isSuccesful = true;
		this.message = message;
	}

}
