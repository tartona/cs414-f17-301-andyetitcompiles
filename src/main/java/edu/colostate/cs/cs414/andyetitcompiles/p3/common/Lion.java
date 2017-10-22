package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Lion extends JunglePiece{
	public Lion(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 7;
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
