package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public enum Color {
	WHITE ("white"),
	BLACK ("black");
	
	private final String name;

	private Color(String s) {
		name = s;
	}
	
	public String toString() {
		return this.name;
	}
}
