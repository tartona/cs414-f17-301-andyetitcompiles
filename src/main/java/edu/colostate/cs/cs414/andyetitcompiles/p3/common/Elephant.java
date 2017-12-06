package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Elephant extends JunglePiece{
	public Elephant(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 8;
		if(color.equals(color.WHITE))
			this.symbol = 'e';
		else
			this.symbol = 'E';
	}
	
	@Override
	public String toString(){
		return "Eleph";
	}

	@Override
	public void restorePower() {
		this.power = 8;
	}
}
