package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import java.util.ArrayList;
import java.util.Scanner;

public class JungleGame implements GameInterface{
	private JungleBoard board;
	private User player1;	//player 1 is white
	private User player2; 	//player 2 is black
	private File register;
	
	public JungleGame(User player1, User player2){
		this.player1 = player1;
		this.player2 = player2;
		board = new JungleBoard();
	}
	
	public JungleGame(){
		board = new JungleBoard();
		register = new File("users.txt");
	}

	public void registerUser(String nickname){
		try {
			Console console = System.console();
			System.out.println(console == null);
			String pass = new String(console.readPassword("Please create a password: "));
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(pass.getBytes());
			byte[]digest1 = m.digest();
			m.update(new String(console.readPassword("Please type your password again: ")).getBytes());
			byte[]digest2 = m.digest();
			BigInteger bigInt1 = new BigInteger(1,digest1);
			String hashtext1 = bigInt1.toString(16);
			BigInteger bigInt2 = new BigInteger(1,digest2);	
			String hashtext2 = bigInt2.toString(16);
			if(hashtext1.equals(hashtext2)){
				Scanner scnr = new Scanner(register);
				File tempfile = new File("temp-file.txt");
				PrintWriter pw = new PrintWriter(tempfile);
				while(scnr.hasNextLine()){
					String line = scnr.nextLine();
					pw.println(line);
				}
				pw.println(nickname + " " + hashtext1);
				if(player1 == null)
					player1 = new User(null, nickname, null);
				else if(player2 == null)
					player2 = new User(null, nickname, null);
				System.out.println("Registered player " + nickname);
				pw.close();
				tempfile.renameTo(register);
			}
			else{
				System.out.println("Passwords did not match. Please try again.");
				registerUser(nickname);
			}
		} catch (FileNotFoundException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public void loginUser(String nickname){
		try {
			Console console = System.console();
			String enteredPassword = new String(console.readPassword("Enter password: "));
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(enteredPassword.getBytes());
			byte[]digest = m.digest();
			BigInteger bigInt2 = new BigInteger(1,digest);	
			String hashtext = bigInt2.toString(16);
			Scanner scnr = new Scanner(register);
			while(!scnr.nextLine().contains(nickname));
			if(hashtext.equals(scnr.nextLine().split(" ")[1])){
				if(player1 == null)
					player1 = new User(null, nickname, null);
				else if(player2 == null)
					player2 = new User(null, nickname, null);
				else
					System.out.println("Sorry, two players are already logged in.");
			}
			else
				System.out.println("Incorrect password entered. Please try again.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void logoutUser(String nickname){
		if(player1.getNickname().equals(nickname))
			player1 = null;
	}
	
	public void unregisterUser(String nickname){
		Scanner scnr;
		try {
			scnr = new Scanner(register);
			File tempfile = new File("temp-file.txt");
			PrintWriter pw = new PrintWriter(tempfile);
			while(scnr.hasNextLine()){
				String line = scnr.nextLine();
				if(line.split(" ")[0].equals(nickname)){
					continue;
				}
				else{
					pw.println(line);
				}
			}
			pw.close();
			tempfile.renameTo(register);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		Console console = System.console();
		Scanner scnr = new Scanner(System.in);
		System.out.println("Welcome to Jungle! To get commands, type 'help'.");
		JungleGame game = new JungleGame();
		System.out.println("Please register or log in two users to start the game.");
		while(game.player1 == null || game.player2 == null){
			String[] query = scnr.nextLine().split(" ");
			if(query[0].equals("register")){
				game.registerUser(query[1]);
			}
			else if(query[0].equals("login")){
				game.loginUser(query[1]);
			}
			else if(query[0].equals("logout")){
				game.logoutUser(query[1]);
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
		scnr.close();
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
