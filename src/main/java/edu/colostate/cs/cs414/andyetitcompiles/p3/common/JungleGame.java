package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.util.ArrayList;

public class JungleGame implements GameInterface{
	private JungleBoard board;
	private ArrayList<JunglePiece> pieces;
	
	public JungleGame(){
		board = new JungleBoard();
	}

	public void makeMove(JunglePiece piece, JungleTile tile) {
		// TODO Auto-generated method stub
	}

	public JungleTile[][] getJungleTiles() {
		return board.getTiles();
	}

	public void capturePiece(JunglePiece attacker, JunglePiece victim){
		if((attacker.getPower() > victim.getPower()) || (attacker instanceof Rat && victim instanceof Elephant)){
			board.movePieceToTile(attacker, victim.getCurrentTile());
			board.removePiece(victim);
		}
	}
	
	public JungleTile[] getValidMoves(JunglePiece piece) {
		if(!pieces.contains(piece))
			return null;
		JungleTile[] moves = new JungleTile[4];
		JungleTile up = board.getTile(piece.getCurrentTile().row - 1, piece.getCurrentTile().col);
		JungleTile down = board.getTile(piece.getCurrentTile().row + 1, piece.getCurrentTile().col);
		JungleTile left = board.getTile(piece.getCurrentTile().row, piece.getCurrentTile().col - 1);
		JungleTile right = board.getTile(piece.getCurrentTile().row, piece.getCurrentTile().col + 1);

		switch(piece.power){
			case 0:
				return moves;
		}
		return null;
	}

	public User getWinner() {
		// TODO Auto-generated method stub
		return null;
	}

}
