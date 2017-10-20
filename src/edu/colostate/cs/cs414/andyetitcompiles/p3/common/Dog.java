package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Dog extends JunglePiece{
	public Dog(Color color, JungleTile startTile){
		super(color, startTile);
		this.power = 3;
	}


	@Override
	public String toString(){
		return "Dog";
	}
}
