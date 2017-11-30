
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public class JungleTile {
	protected TileType type;
	protected JunglePiece currentPiece;
	protected int row;
	protected int col;
	
	public JungleTile(TileType type, int row, int col){
		this.type = type;
		this.row = row;
		this.col = col;
	}
	
	public boolean isEmpty(){
		return this.currentPiece == null;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public TileType getType() {
		return type;
	}

	public void setType(TileType type) {
		this.type = type;
	}

	public JunglePiece getCurrentPiece() {
		return currentPiece;
	}

	public void setCurrentPiece(JunglePiece currentPiece) {
		this.currentPiece = currentPiece;
	}
	
}