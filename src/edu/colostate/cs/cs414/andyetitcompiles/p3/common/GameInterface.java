package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public interface GameInterface {
	public void startGame();
	public void addPlayer(User user);
	public void makeMove(User user, JunglePiece piece, JungleTile tile);
	public TileType[][] getJungleTiles();
	public JungleTile[] getValidMoves(JunglePiece piece);
}
