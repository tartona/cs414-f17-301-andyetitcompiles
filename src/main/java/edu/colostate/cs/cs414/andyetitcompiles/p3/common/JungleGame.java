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
	
	public JungleGame(){
		board = new JungleBoard();
	}

	public boolean readyToPlay(){
		return player1 != null && player2 != null;
	}
	
	public static void main(String[] args){
		Scanner scnr = new Scanner(System.in);
		System.out.println("Welcome to Jungle! To get commands, type 'help'.");
		JungleGame game = new JungleGame();
		System.out.println("Please register two users to start the game.");
		while(game.player1 == null){
			String[] register1 = scnr.nextLine().split(" ");
			if(register1[0].equals("register")){
				game.player1 = new User(null, register1[1], null);
				System.out.println("Registered player " + game.player1.getNickname() + " as P1");
			}
		}
		while(game.player2 == null){
			String[] register2 = scnr.nextLine().split(" ");
			if(register2[0].equals("register")){
				game.player2 = new User(null, register2[1], null);
				System.out.println("Registered player " + game.player2.getNickname() + " as P2");
			}
		}
		
		boolean p1next = true;
		game.board.printBoard();
		while(scnr.hasNext()){
			String[] in = scnr.nextLine().split(" ");
			if((in[0].equalsIgnoreCase(game.player1.getNickname()) && !p1next) || in[0].equalsIgnoreCase(game.player2.getNickname()) && p1next){
				System.out.println("It's not your turn right now.");
				continue;
			}
			Color color = p1next ? Color.WHITE : Color.BLACK;
			String id = in[1];
			JunglePiece piece = game.getPiece(color, id);
			if(piece == null){
				System.out.println("Whoops, I didn't recognize that command. Please try again.");
				continue;
			}
			String direction = in[2];
			int steps = Integer.parseInt(in[3]);
			int row = piece.getCurrentRow();
			int col = piece.getCurrentCol();
			if(direction.equals("up")){
				row -= steps;
			}
			if(direction.equals("down")){
				row += steps;
			}
			if(direction.equals("left")){
				col -= steps;
			}
			if(direction.equals("right")){
				col += steps;
			}
			if(game.isValidMove(piece, game.getTile(row, col))){
				game.makeMove(piece, game.getTile(row, col));
				p1next = !p1next;
			}
			else
				System.out.println("Whoops! Invalid move.");
			if(game.getWinner() != null){
				System.out.println(game.getWinner().getNickname() + " wins the game!");
				break;
			}
			game.board.printBoard();
			System.out.println("It is " + (p1next ? game.player1.getNickname() : game.player2.getNickname())  + "'s turn to move.");		
		}
	}
	
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
		if(pieceOnTile != null && pieceOnTile.getID().equals("elephant") && piece.getID().equals("rat"))
			return true;
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
