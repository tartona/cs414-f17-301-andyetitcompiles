package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleBoard;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

public class GameMessage {
	private int gameID;
	private GameMessageType type;
	private JunglePiece piece;
	private JungleTile tile;
	private JungleBoard board;
	private User player;
	private User winner;
	private User quiter;
	private boolean isTurn;
	
	// For kryo 
	public GameMessage() {}

	/* Move constructor
	 * 
	 * @param piece: piece that is being moved
	 * @param tile: tile that piece is being moved to
	 * @param user: user making the move
	 * @param type: throws exception if type != MAKE_MOVE
	 * 
	 */
	public GameMessage(int gameID, GameMessageType type, JunglePiece piece, JungleTile tile, User player) {
		if(type != GameMessageType.MAKE_MOVE)
			throw new IllegalArgumentException("Type of GameMessage should be MAKE_MOVE");
		if(piece == null || tile == null || player == null)
			throw new IllegalArgumentException("A MAKE_MOVE GameMessage must contain a valid JunglePiece, JungleTile, and user");
		this.gameID = gameID;
		this.type = GameMessageType.MAKE_MOVE;
		this.piece = piece;
		this.tile = tile;
	}
	
	// Update constructor
	public GameMessage(int gameID, GameMessageType type, JungleBoard board) {
		if(type != GameMessageType.UPDATE)
			throw new IllegalArgumentException("Type of GameMessage should be MAKE_MOVE");
		if(board == null)
			throw new IllegalArgumentException("A UPDATE GameMessage must contain a valid JungleBoard");
		this.gameID = gameID;
		this.type = GameMessageType.UPDATE;
		this.board = board;
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
	
	public JunglePiece getPiece() {
		return piece;
	}
	
	public JungleTile getTile() {
		return tile;
	}
	
	public JungleBoard getBoard() {
		return board;
	}
	
	public User getWinner() {
		return winner;
	}
	
	public User getQuiter() {
		return quiter;
	}
	
	public boolean isTurn() {
		return isTurn;
	}
}
