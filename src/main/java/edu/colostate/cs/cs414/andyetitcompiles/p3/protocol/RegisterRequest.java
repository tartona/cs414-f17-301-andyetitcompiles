package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

// RegisterRequests are sent by the client to the server when a client wants to register a new user
public class RegisterRequest {
	String nickname;
	String email;
	String password;
	
	public RegisterRequest(String email, String nickname, String password) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
	}
<<<<<<< HEAD
=======

	public RegisterRequest() {}
>>>>>>> branch 'master' of https://github.com/tartona/cs414-f17-301-andyetitcompiles
	
	public String getEmail() {return email;}
	public String getNickname() {return nickname;}
	public String getPassword() {return password;}
}
