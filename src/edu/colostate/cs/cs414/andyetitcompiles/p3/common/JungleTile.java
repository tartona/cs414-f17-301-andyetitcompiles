package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public class JungleTile {
	protected TileType type;
	
	public JungleTile(TileType type){
		this.type = type;
	}

	public TileType getType() {
		return type;
	}

	public void setType(TileType type) {
		this.type = type;
	}
	
}