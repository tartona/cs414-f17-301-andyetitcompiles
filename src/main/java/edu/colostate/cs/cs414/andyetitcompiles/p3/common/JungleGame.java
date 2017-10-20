package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;
import java.util.ArrayList;

public class JungleGame implements GameInterface{
	private JungleBoard board;
	private ArrayList<JunglePiece> pieces;
	private User player1;	//player 1 is white
	private User player2; 	//player 2 is black
	
	public JungleGame(User player1, User player2){
		this.player1 = player1;
		this.player2 = player2;
		board = new JungleBoard();
	}

	@Override
	public void makeMove(JunglePiece piece, JungleTile tile) {
		// TODO Auto-generated method stub
	}

	@Override
	public JungleTile[][] getJungleTiles() {
		return board.getTiles();
	}

	public boolean capturePiece(JunglePiece attacker, JunglePiece victim){
		if(attacker.getColor() == victim.getColor())	//pieces cannot attack their teammates
			return false;
		
		if((attacker.getPower() > victim.getPower()) || (attacker instanceof Rat && victim instanceof Elephant)){
			board.movePieceToTile(attacker, victim.getCurrentTile());
			board.removePiece(victim);
			return true;
		}
		return false;
	}
	
	public JunglePiece getPiece(Color color, String id){
		return board.getPiece(color, id);
	}
	
	@Override
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

	@Override
	public User getWinner() {
		if(board.getWinner() == Color.WHITE)
			return player1;
		if(board.getWinner() == Color.BLACK)
			return player2;
		return null;
	}

}
