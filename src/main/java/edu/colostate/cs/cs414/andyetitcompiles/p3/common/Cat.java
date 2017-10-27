package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public class Cat extends JunglePiece{
	public Cat(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 2;
	}

	@Override
	public String toString(){
		return "Cat" + (color == Color.WHITE ? 1 : 2);
	}

	@Override
	public void restorePower() {
		this.power = 2;
	}
}
