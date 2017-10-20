package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;
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
	}

}
