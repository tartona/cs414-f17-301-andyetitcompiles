package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Elephant extends JunglePiece{
	public Elephant(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 8;
	}
	
	@Override
	public String toString(){
		return "Elephant";
	}
}
