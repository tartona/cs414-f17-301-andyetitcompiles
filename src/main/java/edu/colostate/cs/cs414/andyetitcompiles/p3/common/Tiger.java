package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Tiger extends JunglePiece{
	public Tiger(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 6;
	}

	@Override
	public String toString(){
		return "Tiger";
	}
}
