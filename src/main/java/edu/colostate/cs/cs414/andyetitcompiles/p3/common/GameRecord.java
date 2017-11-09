package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.sql.Timestamp;

public class GameRecord {
	private User opponent;
	private Timestamp startTime;
	private Timestamp endTime;
	private boolean won;
	private boolean abandoned;
	
	public GameRecord(User opponent, Timestamp startTime, Timestamp endTime, boolean won, boolean abandoned) {
		this.opponent = opponent;
		this.startTime = startTime;
		this.endTime = endTime;
		this.won = won;
		this.abandoned = abandoned;
	}

	public User getOpponent() {
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

}
