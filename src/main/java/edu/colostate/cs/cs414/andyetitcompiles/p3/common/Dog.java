package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Dog extends JunglePiece{
	public Dog(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 3;
		if(color.equals(color.WHITE))
			this.symbol = 'd';
		else
			this.symbol = 'D';
	}

	@Override
	public String toString(){
		return "Dog";
	}

	@Override
	public void restorePower() {
		this.power = 3;
	}
}
