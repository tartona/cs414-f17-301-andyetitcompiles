package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;
import java.util.ArrayList;

public class JungleGame implements GameInterface{
	private JungleBoard board;
	private User player1;	//player 1 is white
	private User player2; 	//player 2 is black
	
	public JungleGame(User player1, User player2){
		this.player1 = player1;
		this.player2 = player2;
		board = new JungleBoard();
	}

	@Override
	public boolean makeMove(JunglePiece piece, JungleTile tile) {
		if(!getValidMoves(piece).contains(tile))
			return false;
		if(tile.getCurrentPiece() != null){
			if(capturePiece(piece, tile.getCurrentPiece())){
				board.movePieceToTile(piece, tile);
				return true;
			}
		}
		board.movePieceToTile(piece, tile);
		return true;
	}

	@Override
	public JungleTile[][] getJungleTiles() {
		return board.getTiles();
	}

	public boolean capturePiece(JunglePiece attacker, JunglePiece victim){
		if(attacker.getColor() == victim.getColor())	//pieces cannot attack their teammates
			return false;	
		if((attacker.getPower() > victim.getPower()) || (attacker instanceof Rat && victim instanceof Elephant)){
			board.removePiece(victim);
			return true;
		}
		return false;
	}
	
	public JunglePiece getPiece(Color color, String id){
		return board.getPiece(color, id);
	}
	
	@Override
	public ArrayList<JungleTile> getValidMoves(JunglePiece piece) {
		ArrayList<JungleTile> moves = new ArrayList<>();
		JungleTile up = board.getTile(piece.getCurrentTile().row - 1, piece.getCurrentTile().col);
		JungleTile down = board.getTile(piece.getCurrentTile().row + 1, piece.getCurrentTile().col);
		JungleTile left = board.getTile(piece.getCurrentTile().row, piece.getCurrentTile().col - 1);
		JungleTile right = board.getTile(piece.getCurrentTile().row, piece.getCurrentTile().col + 1);
		if(piece.getID().equals("lion") || piece.getID().equals("tiger")){	//lion and tiger can both jump across river tiles.
			if(up != null && up.getType() == TileType.RIVER){
				up = board.getTile(piece.getCurrentTile().row - 4, piece.getCurrentTile().col);
			}
			if(down != null && down.getType() == TileType.RIVER){
				down = board.getTile(piece.getCurrentTile().row + 4, piece.getCurrentTile().col);
			}
			if(left != null && left.getType() == TileType.RIVER){
				left = board.getTile(piece.getCurrentTile().row, piece.getCurrentTile().col - 3);
			}
			if(right != null && right.getType() == TileType.RIVER){
				right = board.getTile(piece.getCurrentTile().row, piece.getCurrentTile().col + 3);
			}
		}
		if(isValidMove(piece, up))
			moves.add(up);
		if(isValidMove(piece, down))
			moves.add(down);
		if(isValidMove(piece, left))
			moves.add(left);
		if(isValidMove(piece, right))
			moves.add(right);
		return moves;
	}
	
	public boolean isValidMove(JunglePiece piece, JungleTile tile){
		if(tile == null)
			return false;
		if(tile.getType() == TileType.RIVER && !piece.getID().equals("rat"))
			return false;
		JunglePiece pieceOnTile = tile.getCurrentPiece();
		if(pieceOnTile != null && pieceOnTile.getPower() > piece.getPower())
			return false;
		return true;
	}

	@Override
	public User getWinner() {
		if(board.getWinner() == null)
			return null;
		if(board.getWinner() == Color.WHITE)
			return player1;
		if(board.getWinner() == Color.BLACK)
			return player2;
		return null;
	}
	
	@Override
	public JungleTile getTile(int row, int col){
		return board.getTile(row, col);
	}
	
	@Override
	public void resetGame() {
		board = new JungleBoard();
	}
	
	public JungleBoard getBoard(){
		return this.board;
	}
	
}
