package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

// LoginRequests are sent by the client to the server for login attempts
public class LoginRequest {
	String email;
	String password;
	
	public LoginRequest(String email, String password) { // should probably figure out how to not send the password over plaintext
		this.email = email;
		this.password = password;
	}
	
	public LoginRequest() {}
	
	public String getEmail() {return email;}
	public String getPassword() {return password;}
<<<<<<< HEAD
}
=======
}
>>>>>>> branch 'master' of https://github.com/tartona/cs414-f17-301-andyetitcompiles
