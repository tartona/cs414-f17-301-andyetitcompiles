package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Lion extends JunglePiece{
	public Lion(Color color, JungleTile startTile){
		super(color, startTile);
		this.power = 7;
	}

	@Override
	public String toString(){
		return "Lion";
	}
}
