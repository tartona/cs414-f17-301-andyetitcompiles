
package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Rat;
import java.awt.Color;

public class JungleGameTest {
	JungleGame game;

	@Before
	public void setUp(){
		game = new JungleGame();
	}
	
	@Test
	public void testGetPiece(){
		JunglePiece wrat = game.getPiece(Color.WHITE, "rat");
		assertEquals(6, wrat.getCurrentTile().getCol());
		assertEquals(6, wrat.getCurrentTile().getRow());
	}
	
	@Test
	public void testCapturePiece(){
		JunglePiece wrat = game.getPiece(Color.WHITE, "rat");
		JunglePiece wlion = game.getPiece(Color.WHITE, "lion");
		assertFalse(game.capturePiece(wrat, wlion));
	}
	
}
