package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Cat extends JunglePiece{
	public Cat(Color color, JungleTile startTile){
		super(color, startTile);
		this.power = 2;
	}

	@Override
	public String toString(){
		return "Cat";
	}
}
