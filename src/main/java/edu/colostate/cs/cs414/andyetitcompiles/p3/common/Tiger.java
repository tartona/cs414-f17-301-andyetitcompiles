package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Tiger extends JunglePiece{
	public Tiger(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 6;
		if(color.equals(color.WHITE))
			this.symbol = 't';
		else
			this.symbol = 'T';
	}

	@Override
	public String toString(){
		return "Tiger";
	}

	@Override
	public void restorePower() {
		this.power = 6;
	}
}
