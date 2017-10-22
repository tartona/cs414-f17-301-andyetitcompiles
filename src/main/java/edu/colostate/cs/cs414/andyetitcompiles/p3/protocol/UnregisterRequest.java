package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

public class UnregisterRequest {
	String email;
	String password;

	public UnregisterRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public UnregisterRequest() {}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
