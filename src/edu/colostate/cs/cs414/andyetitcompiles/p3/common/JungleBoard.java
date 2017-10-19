package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public class JungleBoard {
	private final int ROWS = 9;
	private final int COLS = 7;
	
	protected JungleTile[][] tiles;

	public JungleBoard(){
		tiles = new JungleTile[ROWS][COLS];
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
				tiles[i][j] = new JungleTile(type);
			}
		}
	}
	
	public void printBoard(){
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				System.out.print(tiles[i][j].getType() + " ");
			}
			System.out.println();
		}
	}
	
	public int getRows() {
		return ROWS;
	}

	public int getCols() {
		return COLS;
	}
	
}
