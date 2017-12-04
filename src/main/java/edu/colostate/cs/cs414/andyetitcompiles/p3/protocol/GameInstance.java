package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

public class GameInstance {
	int gameID;
	User opponent;
	Color color;
	String board;
	
	// For kryo
	public GameInstance() {}
	
	public GameInstance(int gameID, User opponent, Color color, String board) {
		this.gameID = gameID;
		this.opponent = opponent;
		this.color = color;
		this.board = board;
	}

	public int getGameID() {
		return gameID;
	}

	public User getOpponent() {
		return opponent;
	}

	public Color getColor() {
		return color;
	}
	
	public String getBoard() {
		return board;
	}
	
}
