
package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleBoard;

public class JungleBoardTest {

	protected JungleBoard board;
	
	@Before
	public void setUp(){
		board = new JungleBoard();
	}
	
	@Test
	public void testPrintBoard() {
		board.printBoard();
		fail();
	}
	
	@Test
	public void testGetPiece(){
		assertEquals("lion", board.getPiece(Color.WHITE, "lion").getID());
		fail("Needs additional test cases");
	}
	
	@Test
	public void testIsWon(){
		assertEquals(null, board.getWinner());		
		board.movePieceToTile(board.getPiece(Color.WHITE, "lion"), board.getTile(0, 3));
		assertEquals(Color.WHITE, board.getWinner());
		fail("Needs to be implemented");
	}
	
	@Test
	public void testMovePieceToTile(){
		fail("Needs work");
	}

}
