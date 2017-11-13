package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.util.HashSet;
import java.util.Set;

public class User {
  
	private int id;
    private String email;
	private String nickname;
	private String password;
	private Set<GameRecord> gameRecords;
	private UserStatus status ;
	private JunglePiece piece;
		
	public User(int idUser, String email, String nickname, String password, UserStatus status) {
		this.id = idUser;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.status = status;
		
		gameRecords = new HashSet<>();

		this.piece = null;
	}
	
	public User(int idUser, String email, String nickname, String password, UserStatus status, Set<GameRecord> gameRecords) {
		this.id = idUser;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.status = status;
		
		this.gameRecords = gameRecords;

		this.piece = null;
	}
	
	public User(String email, String nickname, String password) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.piece = null;
		this.status = null;
		this.id=-1;

		gameRecords = new HashSet<>();

	}
	
	public User() {
	}

	public int getId() {return this.id;}
	public String getEmail() {return this.email;}
	public String getPassword() {return this.password;}
	public String getNickname() {return this.nickname;}
	public Set<GameRecord> getRecords(){return this.gameRecords;}
	public UserStatus getStatus() {return this.status;}
	public String setEmail(String email) {return this.email = email;}
	public String setPassword(String password) {return this.password = password;}
	public String setNickname(String nickname) {return this.nickname = nickname;}
	
	public void addRecord(GameRecord record) {
		gameRecords.add(record);
	}
	@Override
	public boolean equals(Object other) {
		if(other instanceof User) {
			User otherUser = (User)other;
			boolean nameMatch =  otherUser.getNickname().equalsIgnoreCase(nickname);
			return nameMatch;
		}
		else
			return false;
	}
	public String toString() {
		String userInfo 
			= "ID: " + id + "\r\n"
			+ "Nickname: " + nickname + "\r\n"
			+ "Email: " + email + "\r\n"
			+ "Status: " + status + "\r\n"
			+ "----Game History----\r\n"
			+ "Opponent Name   | Start Date                   | end Date                     | Won?  | Abandoned? \r\n";
		
		for(GameRecord record:gameRecords) {
			userInfo = userInfo + record + "\r\n";
		}
		
		return userInfo;
	}
	
//	public int hashCode() {
//		int rtn = 0;
//		for(char c:nickname.toCharArray()) {
//			rtn+=c;
//		}
//		return rtn;
//	}
}
