package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;


import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleBoard;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

public class GameMessage {
	private int gameID;
	private GameMessageType type;
	private Color pieceColor;
	private String pieceID;
	private int tileRow;
	private int tileCol;
	private User player;
	private User winner;
	private User quiter;
	private boolean isTurn;
	
	// For kryo 
	public GameMessage() {}

	// Move constructor. This is sent the by the client to the server, then the server forwards it to the other player
	public GameMessage(int gameID, GameMessageType type, Color pieceColor, String pieceID, int tileRow, int tileCol, User player) {
		if(type != GameMessageType.MAKE_MOVE)
			throw new IllegalArgumentException("Type of GameMessage should be MAKE_MOVE");
		this.gameID = gameID;
		this.type = GameMessageType.MAKE_MOVE;
		this.pieceColor = pieceColor;
		this.pieceID = pieceID;
		this.tileRow = tileRow;
		this.tileCol = tileCol;
		this.player = player;
	}
	
	// Game over and quit game constructor
	public GameMessage(int gameID, GameMessageType type, User user) {
		if(user == null)
			throw new IllegalArgumentException("GAME_OVER and QUIT_GAME GameMessages must contain a valid user");
		this.gameID = gameID;
		this.type = type;
		if(type == GameMessageType.GAME_OVER) 
			this.winner = user;
		else if(type == GameMessageType.QUIT_GAME) 
			this.quiter = user;
		else 
			throw new IllegalArgumentException("Type of GameMessage should be GAME_OVER or QUIT_GAME");
	}
	
	// Turn notification constructor
	public GameMessage(int gameID, GameMessageType type, boolean turn) {
		if(type != GameMessageType.SET_TURN)
			throw new IllegalArgumentException("Type of GameMessage should be SET_TURN");
		this.gameID = gameID;
		this.type = type;
		this.isTurn = turn;
	}
	
	public int getGameID() {
		return gameID;
	}
	
	public GameMessageType getType() {
		return type;
	}
	
	public Color getPieceColor() {
		return pieceColor;
	}
	
	public String getPieceID() {
		return pieceID;
	}
	
	public int getTileRow() {
		return tileRow;
	}
	
	public int getTileCol() {
		return tileCol;
	}
	
	public User getWinner() {
		return winner;
	}
	
	public User getQuiter() {
		return quiter;
	}
	
	public User getPlayer() {
		return player;
	}
	public boolean isTurn() {
		return isTurn;
	}
	
}
