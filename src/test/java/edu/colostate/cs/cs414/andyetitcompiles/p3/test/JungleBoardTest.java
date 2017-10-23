
package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleBoard;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;

public class JungleBoardTest {

	protected JungleBoard board;
	
	@Before
	public void setUp(){
		board = new JungleBoard();
	}
	
	@Test
	public void testTraps(){
		JunglePiece wrat = board.getPiece(Color.WHITE, "rat");
		board.movePieceToTile(wrat, board.getTile(8,2));
		assertEquals(0, wrat.getPower());
		board.movePieceToTile(wrat, board.getTile(8, 1));
		assertEquals(1, wrat.getPower());
	}
	
	@Test
	public void testGetPiece(){
		assertEquals("lion", board.getPiece(Color.WHITE, "lion").getID());
		assertEquals("leopard", board.getPiece(Color.BLACK, "leopard").getID());
		assertEquals(null, board.getPiece(Color.CYAN, "elephant"));
		assertEquals(null, board.getPiece(Color.BLACK, "not an animal"));
	}
	
	@Test
	public void testIsWon(){
		assertEquals(null, board.getWinner());		
		board.movePieceToTile(board.getPiece(Color.WHITE, "lion"), board.getTile(0, 3));
		assertEquals(Color.WHITE, board.getWinner());
	}
	
	@Test
	public void testMovePieceToTile(){
		JunglePiece brat = board.getPiece(Color.BLACK, "rat");
		board.movePieceToTile(brat, board.getTile(4,3));
		assertEquals(null, board.getTile(2, 0).getCurrentPiece());
		assertEquals("rat", board.getTile(4, 3).getCurrentPiece().getID());
		assertEquals(4, brat.getCurrentRow());
		assertEquals(3, brat.getCurrentCol());
	}

}
