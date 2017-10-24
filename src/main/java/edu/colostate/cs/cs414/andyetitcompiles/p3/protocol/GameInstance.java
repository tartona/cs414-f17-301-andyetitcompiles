package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import java.awt.Color;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

public class GameInstance {
	int gameID;
	User opponent;
	Color color;
	
	public GameInstance(int gameID, User opponent, Color color) {
		this.gameID = gameID;
		this.opponent = opponent;
		this.color = color;
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
	
}
