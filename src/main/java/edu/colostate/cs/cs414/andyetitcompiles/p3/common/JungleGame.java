package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

//import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import java.util.ArrayList;
import java.util.Scanner;

public class JungleGame implements GameInterface{
	private JungleBoard board;
	private User player1;	//player 1 is white
	private User player2; 	//player 2 is black
	
	public JungleGame(User player1, User player2){
		this.player1 = player1;
		this.player2 = player2;
		board = new JungleBoard();
	}

	public static void main(String[] args){
		Scanner scnr = new Scanner(System.in);
		System.out.println("Initializing board...");
		JungleGame game = new JungleGame(new User("Player1", null, null), new User("Player2", null, null));
		game.board.printBoard();
		while(scnr.hasNext()){
			String[] in = scnr.nextLine().split(" ");
			Color color = (in[0].equals("P1") ? Color.WHITE : Color.BLACK);
			String id = in[1];
			JunglePiece piece = game.getPiece(color, id);
			System.out.println("Moving piece " + piece.id);
			String direction = in[2];
			int row = piece.getCurrentRow();
			int col = piece.getCurrentCol();
			if(direction.equals("up")){
				row--;
			}
			if(direction.equals("down")){
				row++;
			}
			if(direction.equals("left")){
				col--;
			}
			if(direction.equals("row")){
				col++;
			}
			System.out.println(game.getTile(row, col).getType());
			game.makeMove(piece.getColor(), piece.getID(), row, col);
			game.board.printBoard();
		}
	}
	
	public boolean makeMove(Color color, String id, int row, int col) {
		JunglePiece piece = board.getPiece(color, id);
		JungleTile tile = board.getTile(row, col);
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

	public JungleTile[][] getJungleTiles() {
		return board.getTiles();
	}

	public boolean capturePiece(JunglePiece attacker, JunglePiece victim){
		if(attacker.getColor() == victim.getColor())	//pieces cannot attack their teammates
			return false;	
		if(attacker instanceof Elephant && victim instanceof Rat)
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
	
	public ArrayList<JungleTile> getValidMoves(JunglePiece piece) {
		ArrayList<JungleTile> moves = new ArrayList<JungleTile>();
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

	public User getWinner() {
		if(board.getWinner() == null)
			return null;
		if(board.getWinner() == Color.WHITE)
			return player1;
		if(board.getWinner() == Color.BLACK)
			return player2;
		return null;
	}
	
	public JungleTile getTile(int row, int col){
		return board.getTile(row, col);
	}
	
	public void resetGame() {
		board = new JungleBoard();
	}
	
	public JungleBoard getBoard(){
		return this.board;

	}

	public void updateBoard(JungleBoard board) {
		this.board = board;
	}
	
}
