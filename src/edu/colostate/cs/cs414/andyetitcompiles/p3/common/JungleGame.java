package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.util.ArrayList;

public class JungleGame implements GameInterface{
	private JungleBoard board;
	private ArrayList<JunglePiece> pieces;
	
	public JungleGame(){
		board = new JungleBoard();
	}
	
	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPlayer(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeMove(User user, JunglePiece piece, JungleTile tile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TileType[][] getJungleTiles() {
		return board.getTiles();
	}

	@Override
	public JungleTile[] getValidMoves(JunglePiece piece) {
		if(!pieces.contains(piece))
			return null;
		return null;
	}

}
