package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Rat extends JunglePiece {
	public Rat(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 1;	
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
