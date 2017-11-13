package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.sql.Timestamp;

public class GameRecord {
	private int iduser;
	private String opponent;
	private Timestamp startTime;
	private Timestamp endTime;
	private boolean won;
	private boolean abandoned;
	
	// For kryo
	public GameRecord() {}
	
	public GameRecord(int idUser, String opponent, Timestamp startTime, Timestamp endTime, boolean won, boolean abandoned) {
		this.iduser = idUser;
		this.opponent = opponent;
		this.startTime = startTime;
		this.endTime = endTime;
		this.won = won;
		this.abandoned = abandoned;
	}

	public int getIdUser() {
		return iduser;
	}
	public String getOpponent() {
		return opponent;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public boolean isWon() {
		return won;
	}

	public boolean isAbandoned() {
		return abandoned;
	}
	
	public String toString() {
		String rtnRecord = String.format("%-15s | %tc | %tc | %-5s | %-5s", opponent,startTime,endTime,won,abandoned);
		
		return rtnRecord;
	}
	
}
