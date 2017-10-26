package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Wolf extends JunglePiece{
	public Wolf(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 4;
	}
	
	@Override
	public String toString(){
		return "Wolf" + (color == Color.WHITE ? 1 : 2);
	}

	@Override
	public void restorePower() {
		this.power = 4;
	}
	
	
}
