package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

// UserRequests are sent by the client to the server when the client wants to query the server 
// for a registered user
public class UserRequest {
	String nickname;

	public UserRequest(String nickname) {
		this.nickname = nickname;
	}
	
<<<<<<< HEAD
	public String getNickname() {return nickname;}
}
=======
	public UserRequest() {}

	public String getNickname() {
		return nickname;
	}
}
>>>>>>> branch 'master' of https://github.com/tartona/cs414-f17-301-andyetitcompiles
