
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;
import java.util.ArrayList;

public class JungleBoard {
	private final int ROWS = 9;
	private final int COLS = 7;
	
	private JungleTile[][] tiles = new JungleTile[ROWS][COLS];
	private ArrayList<JunglePiece> pieces = new ArrayList<>();

	public JungleBoard(){
		initializeBoard();
		initializePieces();
	}
	
	private void initializeWhiteTeam(){
		JungleTile ratTile = tiles[6][6];
		JunglePiece rat = new Rat(Color.WHITE, ratTile);
		ratTile.setCurrentPiece(rat);
		pieces.add(rat);
		
		JungleTile catTile = tiles[7][1];
		JunglePiece cat = new Cat(Color.WHITE, catTile);
		catTile.setCurrentPiece(cat);
		pieces.add(cat);
		
		JungleTile dogTile = tiles[7][5];
		JunglePiece dog = new Dog(Color.WHITE, dogTile);
		dogTile.setCurrentPiece(dog);
		pieces.add(dog);
		
		JungleTile wolfTile = tiles[6][2];
		JunglePiece wolf = new Wolf(Color.WHITE, wolfTile);
		wolfTile.setCurrentPiece(wolf);
		pieces.add(wolf);
		
		JungleTile lionTile = tiles[8][6];
		JunglePiece lion = new Lion(Color.WHITE, lionTile);
		lionTile.setCurrentPiece(lion);
		pieces.add(lion);
		
		JungleTile leopardTile = tiles[6][4];
		JunglePiece leopard = new Leopard(Color.WHITE, leopardTile);
		leopardTile.setCurrentPiece(leopard);
		pieces.add(leopard);
		
		JungleTile tigerTile = tiles[8][0];
		JunglePiece tiger = new Tiger(Color.WHITE, tigerTile);
		tigerTile.setCurrentPiece(tiger);
		pieces.add(tiger);
		
		JungleTile elephantTile = tiles[6][0];
		JunglePiece elephant = new Elephant(Color.WHITE, elephantTile);
		elephantTile.setCurrentPiece(elephant);
		pieces.add(elephant);
	}
	
	private void initializeBlackTeam(){
		JungleTile ratTile = tiles[ROWS - 1 - 6][COLS - 1 - 6];
		JunglePiece rat = new Rat(Color.BLACK, ratTile);
		ratTile.setCurrentPiece(rat);
		pieces.add(rat);
		
		JungleTile catTile = tiles[ROWS - 1 - 7][COLS - 1 - 1];
		JunglePiece cat = new Cat(Color.BLACK, catTile);
		catTile.setCurrentPiece(cat);
		pieces.add(cat);
		
		JungleTile dogTile = tiles[ROWS - 1 - 7][COLS - 1 - 5];
		JunglePiece dog = new Dog(Color.BLACK, dogTile);
		dogTile.setCurrentPiece(dog);
		pieces.add(dog);
		
		JungleTile wolfTile = tiles[ROWS - 1 - 6][COLS - 1 - 2];
		JunglePiece wolf = new Wolf(Color.BLACK, wolfTile);
		wolfTile.setCurrentPiece(wolf);
		pieces.add(wolf);
		
		JungleTile lionTile = tiles[ROWS - 1 - 8][COLS - 1 - 6];
		JunglePiece lion = new Lion(Color.BLACK, lionTile);
		lionTile.setCurrentPiece(lion);
		pieces.add(lion);
		
		JungleTile leopardTile = tiles[ROWS - 1 - 6][COLS - 1 - 4];
		JunglePiece leopard = new Leopard(Color.BLACK, leopardTile);
		leopardTile.setCurrentPiece(leopard);
		pieces.add(leopard);
		
		JungleTile tigerTile = tiles[ROWS - 1 - 8][COLS - 1 - 0];
		JunglePiece tiger = new Tiger(Color.BLACK, tigerTile);
		tigerTile.setCurrentPiece(tiger);
		pieces.add(tiger);
		
		JungleTile elephantTile = tiles[ROWS - 1 - 6][COLS - 1 - 0];
		JunglePiece elephant = new Elephant(Color.BLACK, elephantTile);
		elephantTile.setCurrentPiece(elephant);
		pieces.add(elephant);
	}
	
	public void initializePieces(){
		initializeWhiteTeam();
		initializeBlackTeam();
	}
	
	public void initializeBoard(){
		TileType type;
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				type = TileType.NORMAL;
				if((i == 0 && j == 3) || (i == 8 && j == 3))
					type = TileType.DEN;
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
	}
	
	public void removePiece(JunglePiece piece){
		pieces.remove(piece);
	}
	
	public boolean isWon(){ 
		return (tiles[0][3].getCurrentPiece().getColor() == Color.WHITE) || (tiles[8][3].getCurrentPiece().getColor() == Color.BLACK);
	}
	
	public JungleTile getTile(int row, int col){
		if(row >= ROWS)
			return null;
		if(col >= COLS)
			return null;
		return tiles[row][col];
	}
	
	public void printBoard(){
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				if(tiles[i][j].getCurrentPiece() == null)
					System.out.print(tiles[i][j].getType() + "\t");
				else
					System.out.print(tiles[i][j].getCurrentPiece().toString() + " \t");
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
