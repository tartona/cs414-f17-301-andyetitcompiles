
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import java.util.ArrayList;

public class JungleBoard {
	private final int ROWS = 9;
	private final int COLS = 7;
	
	private JungleTile[][] tiles = new JungleTile[ROWS][COLS];
	private ArrayList<JunglePiece> pieces = new ArrayList<JunglePiece>();

	public JungleBoard(){
		initializeBoard();
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
	
	public void initializePieces(){
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
	
	public void initializeBoard(){
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
