package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Lion extends JunglePiece{
	public Lion(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 7;
		if(color.equals(color.WHITE))
			this.symbol = 'l';
		else
			this.symbol = 'L';
	}

	@Override
	public String toString(){
		return "Lion";
	}

	@Override
	public void restorePower() {
		this.power = 7;
	}
}
