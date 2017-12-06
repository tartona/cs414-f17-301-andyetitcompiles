
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

public abstract class JunglePiece {

	protected int power;
	protected Color color;
	protected JungleTile currentTile;
	protected String id;
	// Represents a piece when it is stored in a string in the database. Lowercase is white, uppercase is black
	// R = rat, C = cat, D = dog, W = wolf, J = leopard(jaguar), T = tiger, L = lion, E = elephant
	protected char symbol;
	
	public JunglePiece(Color color, JungleTile startTile, String id){
		this.color = color;
		this.currentTile = startTile;
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof JunglePiece){
			return (((JunglePiece) o).getColor() == this.color) && (((JunglePiece) o ).getID().equals(this.id));
		}
		return false;
	}
	
	public int getCurrentRow(){
		return currentTile.getRow();
	}
	
	public int getCurrentCol(){
		return currentTile.getCol();
	}
	
	public abstract void restorePower();
	
	public String getID(){
		return this.id;
	}
	
	public char getSymbol() {
		return symbol;
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
