package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Rat extends JunglePiece {
	public Rat(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 1;	
		if(color.equals(color.WHITE))
			this.symbol = 'r';
		else
			this.symbol = 'R';
	}
	
	@Override
	public String toString(){
		return "Rat";
	}

	@Override
	public void restorePower() {
		this.power = 1;
	}
	
	
}
