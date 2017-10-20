package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

public class UnregisterResponse {
	boolean isSuccessful;
	String message;
	
	public UnregisterResponse(boolean isSuccessful, String message) {
		this.isSuccessful = isSuccessful;
		this.message = message;
	}
	
	public UnregisterResponse() {}

	public boolean successful() {
		return isSuccessful;
	}

	public String getMessage() {
		return message;
	}
	
}
