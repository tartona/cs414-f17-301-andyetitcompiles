package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Dog extends JunglePiece{
	public Dog(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 3;
	}

	@Override
	public String toString(){
		return "Dog" + (color == Color.WHITE ? 1 : 2);
	}

	@Override
	public void restorePower() {
		this.power = 3;
	}
}
