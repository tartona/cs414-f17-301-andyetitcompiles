package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public abstract class JunglePiece {
	
	public JunglePiece(Color color, JungleTile startTile){
		this.color = color;
		this.currentTile = startTile;
	}
	
	protected int power;
	protected Color color;
	protected JungleTile currentTile;
}
