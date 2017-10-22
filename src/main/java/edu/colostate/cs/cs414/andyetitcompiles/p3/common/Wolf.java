package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Wolf extends JunglePiece{
	public Wolf(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 4;
	}
	
	@Override
	public String toString(){
		return "Wolf";
	}

	@Override
	public void restorePower() {
		this.power = 4;
	}
	
	
}
