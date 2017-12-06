package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.colostate.cs.cs414.andyetitcompiles.p3.client.ClientGameController;
import edu.colostate.cs.cs414.andyetitcompiles.p3.client.JungleClient;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.server.JungleServer;
import edu.colostate.cs.cs414.andyetitcompiles.p3.server.ServerGameController;

public class ServerClientIntegrationTest {
	private JungleClient client1;
	private JungleClient client2;
	private JungleServer server;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// Start server and client
		server = new JungleServer();
		Thread.sleep(1000);
		client1 = new JungleClient();
		client2 = new JungleClient();
		Thread.sleep(1000);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
		client1.stop();
		client2.stop();
		server = null;
		client1 = null;
		client2 = null;
	}

	// Messy test with not very many asserts, mainly used this to look at the console to see if the right messages are printing
	@Test
	public void testServerClientIntegration() throws InterruptedException {
		// Test login and register
		client1.register("email@email.com", "nick", "pass");
		client1.login("email@email.com", "pass");
		Thread.sleep(100);
		assertEquals(new User(null, "nick", null), client1.getClientUser());
		// Test finding another user
		client2.register("other@email.com", "jimmy", "pass");
		client2.login("other@email.com", "pass");
		Thread.sleep(100);
		client1.findUser("jimmy");
		Thread.sleep(100);
		assertEquals(new User(null, "jimmy", null), client1.getRequestedUser());
		// Test inviting another user
		client1.invite(client1.getRequestedUser());
		Thread.sleep(500);
		// Test making game moves
		ClientGameController client1Controller = client1.getController(0);
		ClientGameController client2Controller = client2.getController(0);
		ServerGameController serverController = server.getController(0);
		JunglePiece piece = client1Controller.getBoard().getPiece(Color.WHITE, "rat");
		int[] move = client1Controller.getGame().getValidMoves(piece.getColor(), piece.getID()).get(0);
		client1Controller.makeMove(piece, move[0], move[1]);
		Thread.sleep(500);
	}

}
