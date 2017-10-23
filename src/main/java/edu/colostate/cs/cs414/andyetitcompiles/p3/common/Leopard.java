package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.awt.Color;

public class Leopard extends JunglePiece{
	public Leopard(Color color, JungleTile startTile, String id){
		super(color, startTile, id);
		this.power = 5;
	}

	@Override
	public String toString(){
		return "Leopard";
	}

	@Override
	public void restorePower() {
		this.power = 5;
	}
}
