package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public interface GameInterface {
	public void makeMove(JunglePiece piece, JungleTile tile);
	public JungleTile[][] getJungleTiles();
	public JungleTile[] getValidMoves(JunglePiece piece);
	public User getWinner();
}
