package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Cat extends JunglePiece{
	public Cat(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 2;
	}

	@Override
	public String toString(){
		return "Cat";
	}

	@Override
	public void restorePower() {
		this.power = 2;
	}
}
