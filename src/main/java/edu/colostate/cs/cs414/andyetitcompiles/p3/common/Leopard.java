package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Leopard extends JunglePiece{
	public Leopard(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 5;
		if(color.equals(color.WHITE))
			this.symbol = 'j';
		else
			this.symbol = 'J';
	}

	@Override
	public String toString(){
		return "Leopard";
	}

	@Override
	public void restorePower() {
		this.power = 5;
	}
}
