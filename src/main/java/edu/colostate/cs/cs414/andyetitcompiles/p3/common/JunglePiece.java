
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public abstract class JunglePiece {

	protected int power;
	protected Color color;
	protected JungleTile currentTile;
	
	public JunglePiece(Color color, JungleTile startTile){
		this.color = color;
		this.currentTile = startTile;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public JungleTile getCurrentTile() {
		return currentTile;
	}

	public void setCurrentTile(JungleTile currentTile) {
		this.currentTile = currentTile;
	}
	
	
}
