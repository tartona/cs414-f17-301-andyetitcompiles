package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public interface GameInterface {
	/**
	 * Checks to see if suggested move is allowed given current game state. If necessary, will call capturePiece. If move is allowed,
	 * then the board will be updated.
	 * @param piece Piece making the move
	 * @param tile	Tile where piece is attempting to move
	 */
	public void makeMove(JunglePiece piece, JungleTile tile);
	
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
	public JungleTile[] getValidMoves(JunglePiece piece);
	
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
