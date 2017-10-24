package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

public class ServerGameControllerTest {
	Server server;
	Client client1;
	Client client2;
	User player1;
	User player2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testStartGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testGameWon() {
		fail("Not yet implemented");
	}

	@Test
	public void testMoveReceived() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testQuitGameReceived() {
		fail("Not yet implemented");
	}
}
