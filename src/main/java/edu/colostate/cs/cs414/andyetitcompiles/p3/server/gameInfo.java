package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.sql.Timestamp;

public class gameInfo {
	private int gameID;
	private int user1;
	private int user2;
	private Timestamp startTimestamp;
	private int playerTurn;
	private String gameConfig;
	
	
	public gameInfo(int gameID, int user1, int user2, Timestamp startTime, int playerTurn, String gameConfig) {
		this.gameID = gameID;
		this.user1 = user1;
		this.user2 = user2;
		this.startTimestamp = startTime;
		this.playerTurn = playerTurn;
		this.gameConfig = gameConfig;
	}
	
	
	public int getGameID() {
		return gameID;
	}
	public int getUser1() {
		return user1;
	}
	public int getUser2() {
		return user2;
	}
	public Timestamp getStartTimestamp() {
		return startTimestamp;
	}
	public int getPlayerTurn() {
		return playerTurn;
	}
	public String getGameConfig() {
		return gameConfig;
	}
}
