package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import java.util.ArrayList;

public interface GameInterface {
	/**
	 * Checks to see if suggested move is allowed given current game state. If necessary, will call capturePiece. If move is allowed,
	 * then the board will be updated.
	 * @param piece Piece making the move
	 * @param tile	Tile where piece is attempting to move
	 * @return True if move is successful, false if move was not allowed
	 */
	public boolean makeMove(JunglePiece piece, JungleTile tile);
	
	/**
	 * Returns the JunglePiece with specified color and ID
	 * @param color Either Color.BLACK or Color.WHITE
	 * @param id name of animal represented by JunglePiece (i.e. "cat", "dog", "elephant")
	 * @return
	 */
	public JunglePiece getPiece(Color color, String id);
	
	/**
	 * Returns JungleTile located at (row, col) using standard array indexing so (0,0) is the upper leftmost spot.
	 * @param row
	 * @param col
	 * @return
	 */
	public JungleTile getTile(int row, int col);
	
	/**
	 * 
	 * @return 2D array holding all of the tiles in current game state
	 */
	public JungleTile[][] getJungleTiles();
	
	/**
	 * Checks for all valid moves available to a specific piece, given the game's current state
	 * @param piece
	 * @return array holding 0 to 4 permissable moves from the piece's current location
	 */
	public ArrayList<JungleTile> getValidMoves(JunglePiece piece);
	
	/**
	 * Restarts the game and resets board to initial state with the same users as before.
	 */
	public void resetGame();
	
	/**
	 * @return User that has won the game, null if game is not yet won.
	 */
	public User getWinner();
	
	/**
	 * Checks to see if an attack is allowed given the current state of the board.
	 * @param attacker	Piece trying to attack
	 * @param victim	Piece being attacked
	 * @return true if attack is successful (board is also updated within this method), false otherwise (board is not changed)
	 */
	public boolean capturePiece(JunglePiece attacker, JunglePiece victim);
}
