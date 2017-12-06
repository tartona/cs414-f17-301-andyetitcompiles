
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import java.util.ArrayList;

public class JungleBoard {
	// Constants
	private final int ROWS = 9;
	private final int COLS = 7;

	
	private JungleTile[][] tiles = new JungleTile[ROWS][COLS];
	private ArrayList<JunglePiece> pieces = new ArrayList<JunglePiece>();

	public JungleBoard(){
		initializeBoard();
		initializePieces();
	}
	
	// Use this constructor to create a board from a stored configuration
	public JungleBoard(String board) {
		initializeBoard();
		if(board != null)
			loadPieces(board);
		else
			initializePieces();
	}
	
	private void initializeWhiteTeam(){
		JungleTile ratTile = tiles[6][6];
		JunglePiece rat = new Rat(Color.WHITE, ratTile, "rat");
		ratTile.setCurrentPiece(rat);
		pieces.add(rat);
		
		JungleTile catTile = tiles[7][1];
		JunglePiece cat = new Cat(Color.WHITE, catTile, "cat");
		catTile.setCurrentPiece(cat);
		pieces.add(cat);
		
		JungleTile dogTile = tiles[7][5];
		JunglePiece dog = new Dog(Color.WHITE, dogTile, "dog");
		dogTile.setCurrentPiece(dog);
		pieces.add(dog);
		
		JungleTile wolfTile = tiles[6][2];
		JunglePiece wolf = new Wolf(Color.WHITE, wolfTile, "wolf");
		wolfTile.setCurrentPiece(wolf);
		pieces.add(wolf);
		
		JungleTile lionTile = tiles[8][6];
		JunglePiece lion = new Lion(Color.WHITE, lionTile, "lion");
		lionTile.setCurrentPiece(lion);
		pieces.add(lion);
		
		JungleTile leopardTile = tiles[6][4];
		JunglePiece leopard = new Leopard(Color.WHITE, leopardTile, "leopard");
		leopardTile.setCurrentPiece(leopard);
		pieces.add(leopard);
		
		JungleTile tigerTile = tiles[8][0];
		JunglePiece tiger = new Tiger(Color.WHITE, tigerTile, "tiger");
		tigerTile.setCurrentPiece(tiger);
		pieces.add(tiger);
		
		JungleTile elephantTile = tiles[6][0];
		JunglePiece elephant = new Elephant(Color.WHITE, elephantTile, "elephant");
		elephantTile.setCurrentPiece(elephant);
		pieces.add(elephant);
	}
	
	private void initializeBlackTeam(){
		JungleTile ratTile = tiles[ROWS - 1 - 6][COLS - 1 - 6];
		JunglePiece rat = new Rat(Color.BLACK, ratTile, "rat");
		ratTile.setCurrentPiece(rat);
		pieces.add(rat);
		
		JungleTile catTile = tiles[ROWS - 1 - 7][COLS - 1 - 1];
		JunglePiece cat = new Cat(Color.BLACK, catTile, "cat");
		catTile.setCurrentPiece(cat);
		pieces.add(cat);
		
		JungleTile dogTile = tiles[ROWS - 1 - 7][COLS - 1 - 5];
		JunglePiece dog = new Dog(Color.BLACK, dogTile, "dog");
		dogTile.setCurrentPiece(dog);
		pieces.add(dog);
		
		JungleTile wolfTile = tiles[ROWS - 1 - 6][COLS - 1 - 2];
		JunglePiece wolf = new Wolf(Color.BLACK, wolfTile, "wolf");
		wolfTile.setCurrentPiece(wolf);
		pieces.add(wolf);
		
		JungleTile lionTile = tiles[ROWS - 1 - 8][COLS - 1 - 6];
		JunglePiece lion = new Lion(Color.BLACK, lionTile, "lion");
		lionTile.setCurrentPiece(lion);
		pieces.add(lion);
		
		JungleTile leopardTile = tiles[ROWS - 1 - 6][COLS - 1 - 4];
		JunglePiece leopard = new Leopard(Color.BLACK, leopardTile, "leopard");
		leopardTile.setCurrentPiece(leopard);
		pieces.add(leopard);
		
		JungleTile tigerTile = tiles[ROWS - 1 - 8][COLS - 1 - 0];
		JunglePiece tiger = new Tiger(Color.BLACK, tigerTile, "tiger");
		tigerTile.setCurrentPiece(tiger);
		pieces.add(tiger);
		
		JungleTile elephantTile = tiles[ROWS - 1 - 6][COLS - 1 - 0];
		JunglePiece elephant = new Elephant(Color.BLACK, elephantTile, "elephant");
		elephantTile.setCurrentPiece(elephant);
		pieces.add(elephant);
	}
	
	private void initializePieces(){
		initializeWhiteTeam();
		initializeBlackTeam();
	}
	
	public JunglePiece getPiece(Color color, String id){
		for(JunglePiece piece: pieces){
			if(piece.getColor() == color && id.equalsIgnoreCase(piece.getID())){
				return piece;
			}
		}
		return null;
	}
	
	public ArrayList<JunglePiece> getPieces(Color color){
		ArrayList<JunglePiece> result = new ArrayList<>();
		for(JunglePiece piece: pieces){
			if(piece.getColor() == color){
				result.add(piece);
			}
		}
		return result;
	}
	
	private void initializeBoard(){
		TileType type;
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				type = TileType.NORMAL;
				if(i == 0 && j == 3) 
					type = TileType.B_DEN;
				if(i == 8 && j == 3)
					type = TileType.W_DEN;
				if((i <= 5 && i >= 3) && (j == 1 || j == 2 || j == 4 || j== 5))
					type = TileType.RIVER;
				if((i == 0 || i == 8) && (j == 2 || j == 4))
					type = TileType.TRAP;
				if((i == 1 && j == 3) || (i == 7 && j == 3))
					type = TileType.TRAP;
				tiles[i][j] = new JungleTile(type, i, j);
			}
		}
	}
	
	public void movePieceToTile(JunglePiece piece, JungleTile tile){
		if(tile.getType() == TileType.TRAP)
			piece.setPower(0);
		else if(piece.getPower() == 0)
			piece.restorePower();
		piece.getCurrentTile().setCurrentPiece(null);
		piece.setCurrentTile(tile);
		tile.setCurrentPiece(piece);
	}
	
	public void removePiece(JunglePiece piece){
		pieces.remove(piece);
		piece.getCurrentTile().setCurrentPiece(null);
	}
	
	public Color getWinner(){ 
		if(tiles[0][3].getCurrentPiece() == null && tiles[8][3].getCurrentPiece() == null)
			return null;
		if(tiles[0][3].getCurrentPiece() != null && tiles[0][3].getCurrentPiece().getColor() == Color.WHITE)
			return Color.WHITE;
		if(tiles[8][3].getCurrentPiece() != null && tiles[8][3].getCurrentPiece().getColor() == Color.BLACK)
			return Color.BLACK;
		return null;
	}
	
	public JungleTile getTile(int row, int col){
		if(row < 0 || row >= ROWS)
			return null;
		if(col < 0 || col >= COLS)
			return null;
		return tiles[row][col];
	}
	
	public void printBoard(){
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				if(tiles[i][j].getCurrentPiece() == null)
					System.out.print(tiles[i][j].getType() + "\t");
				else
					System.out.print(tiles[i][j].getCurrentPiece().toString() + "\t");
			}
			System.out.println();
		}
	}
	
	// Creates and returns a string representation of the current board configuration. Used to store game state
	public String getBoardRepresentation() {
		JungleTile[][] tiles = getTiles();
		String result = "";
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLS; j++) {
				if(tiles[i][j].getCurrentPiece() == null) {
					result = result + 0;
				}
				else {
					result = result + tiles[i][j].getCurrentPiece().getSymbol();
				}
			}
		}
		return result;
	}
	
	// Loads a board configuration from a string representation
	private void loadPieces(String board) {
		int stringIndex = 0;
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j <COLS; j++) {
				JunglePiece piece = null;
				switch(board.charAt(stringIndex)) {
				case 'r':
					piece = new Rat(Color.WHITE, tiles[i][j], "rat");
					break;
				case 'R':
					piece = new Rat(Color.BLACK, tiles[i][j], "rat");
					break;
				case 'c':
					piece = new Cat(Color.WHITE, tiles[i][j], "cat");
					break;
				case 'C':
					piece = new Cat(Color.BLACK, tiles[i][j], "cat");
					break;
				case 'd':
					piece = new Dog(Color.WHITE, tiles[i][j], "dog");
					break;
				case 'D':
					piece = new Dog(Color.BLACK, tiles[i][j], "dog");
					break;
				case 'w':
					piece = new Wolf(Color.WHITE, tiles[i][j], "wolf");
					break;
				case 'W':
					piece = new Wolf(Color.BLACK, tiles[i][j], "wolf");
					break;
				case 'j':
					piece = new Leopard(Color.WHITE, tiles[i][j], "leopard");
					break;
				case 'J':
					piece = new Leopard(Color.BLACK, tiles[i][j], "leopard");
					break;
				case 't':
					piece = new Tiger(Color.WHITE, tiles[i][j], "tiger");
					break;
				case 'T':
					piece = new Tiger(Color.BLACK, tiles[i][j], "tiger");
					break;
				case 'l':
					piece = new Lion(Color.WHITE, tiles[i][j], "lion");
					break;
				case 'L':
					piece = new Lion(Color.BLACK, tiles[i][j], "lion");
					break;
				case 'e':
					piece = new Elephant(Color.WHITE, tiles[i][j], "elephant");
					break;
				case 'E':
					piece = new Elephant(Color.BLACK, tiles[i][j], "elephant");
					break;
				}
				if(piece != null) {
					pieces.add(piece);
					tiles[i][j].setCurrentPiece(piece);
				}
				stringIndex++;
			}
		}
	}
	
	public JungleTile[][] getTiles(){
		return tiles;
	}
	
	public int getRows() {
		return ROWS;
	}

	public int getCols() {
		return COLS;
	}
	
}
