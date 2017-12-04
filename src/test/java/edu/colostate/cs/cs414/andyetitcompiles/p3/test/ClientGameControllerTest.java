package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.colostate.cs.cs414.andyetitcompiles.p3.client.ClientGameController;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleBoard;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessage;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessageType;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.Network;

public class ClientGameControllerTest {
	static KryoServerMock server;
	Client client;
	Object lastReceived;
	int gameID;
	User player1;
	User player2;
	ClientGameController controller;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		server = new KryoServerMock();
		Thread.sleep(500);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		server.stop();
		server = null;
	}
	
	@Before
	public void setUp() throws IOException, InterruptedException {
		// Create and connect a client for every test
		player1 = new User(null, "player1", null);
		player2 = new User(null, "player2", null);
		gameID = 1;
		client = new Client();
		client.start();
		Network.register(client);
		client.addListener(new Listener() {
			@Override
			public void connected(Connection c) {
				System.out.println("Successfulyl connected to server");
			}
			@Override
			public void received(Connection c, Object o) {
				lastReceived = o;
				System.out.println("Client received object" + o.getClass());
				if(o instanceof GameMessage) {
					controller.handleMessage((GameMessage)o);
				}
			}
		});
		new Thread("Connect") {
			public void run () {
				try {
					client.connect(5000, Network.host, Network.port);
				} catch (IOException ex) {
					System.out.println("Something went wrong while connecting to the server: " + ex.getMessage());
				}
			}
		}.start();
		Thread.sleep(500);
		controller = new ClientGameController(gameID, player1, player2, Color.WHITE, null, client);
	}
	
	@After
	public void tearDown() {
		controller = null;
		client.close();
		client = null;
	}

	@Test
	public void testMakeMoveValid() throws InterruptedException {
		// figure out a valid move
		JungleGame game = controller.getGame();
		JunglePiece piece = game.getPiece(Color.WHITE, "rat");
		int[] move = game.getValidMoves(Color.WHITE, "rat").get(0);
		// Set the turn and make the move, then wait for a response
		controller.setTurn(true);
		controller.makeMove(piece, move[0], move[1]);
		Thread.sleep(500);
		Object serverReceived = server.getLastReceived();
		if(serverReceived instanceof GameMessage) {
			GameMessage message = (GameMessage)serverReceived;
			assertEquals(GameMessageType.MAKE_MOVE, message.getType());
			assertEquals(Color.WHITE, message.getPieceColor());
			assertEquals("rat", message.getPieceID());
			assertEquals(move[0], message.getTileRow());
			assertEquals(move[1], message.getTileCol());
		}
		else
			fail("Incorrect object received by server");
	}
	
	@Test
	public void testMakeMoveInvalid() {
		fail("Not implemented yet");
	}
	
	@Test
	public void testMakeMoveNotTurn() {
		fail("Not implemented yet");
	}
	
	@Test
	public void testMakeMoveWrongColor() {
		fail("Not implemented yet");
	}

	@Test
	public void testQuitGame() throws InterruptedException {
		controller.quitGame();
		Thread.sleep(500);
		if(server.getLastReceived() instanceof GameMessage) {
			GameMessage message = (GameMessage)server.getLastReceived();
			assertEquals(message.getType(), GameMessageType.QUIT_GAME);
			assertEquals(message.getQuiter(), player1);
		}
		else 
			fail("incorrect object received by server");
	}

	@Test
	public void testReceivedMove() throws InterruptedException {
		// figure out a valid move
		JungleGame game = controller.getGame();
		JunglePiece piece = game.getPiece(Color.BLACK, "rat");
		int[] nextMove = game.getValidMoves(piece.getColor(), piece.getID()).get(0);
		GameMessage gameUpdate = new GameMessage(gameID, GameMessageType.MAKE_MOVE, piece.getColor(), piece.getID(), nextMove[0], nextMove[1], player2);
		client.sendTCP(gameUpdate);
		Thread.sleep(500);
		if(lastReceived instanceof GameMessage) {
			GameMessage received = (GameMessage)lastReceived;
			// Maybe there should be a way to check if the board state has changed easier than this, like equals override for jungleboard.
			// I am checking to see if the black rats first valid move has changed
			assertNotEquals(nextMove, game.getValidMoves(piece.getColor(), piece.getID()).get(0));
		}
		else
			fail("incorrect object received by client");
	}
	
	@Test
	public void testGameOverReceived() throws InterruptedException {
		fail("Not implemented yet");
	}
	
	@Test
	public void testSetTurnReceived() {
		fail("Not implemented yet");
	}
}
