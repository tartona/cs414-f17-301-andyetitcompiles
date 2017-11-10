
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public class User {
  
    private String email;
	private String password;
	private String nickname;
	private GameRecord GR = new GameRecord();
	private UserStatus ustatus;
	private JunglePiece piece;
		
	public User(String email, String nickname, String password) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
	}
	
	public User() {}
	
	public String getEmail() {return this.email;}
	public String getPassword() {return this.password;}
	public String getNickname() {return this.nickname;}
	public String setEmail(String email) {return this.email = email;}
	public String setPassword(String password) {return this.password = password;}
	public String setNickname(String nickname) {return this.nickname = nickname;}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof User) {
			User otherUser = (User)other;
			return otherUser.getNickname().equals(nickname);
		}
		else
			return false;
	}
	
	@Override
	public String toString() {
		return nickname;
	}
}
