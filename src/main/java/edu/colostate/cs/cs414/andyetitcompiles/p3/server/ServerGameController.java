package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.sql.Timestamp;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessage;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessageType;

public class ServerGameController {
	JungleGame game;
	int gameID;
	JungleClientConnection player1;
	JungleClientConnection player2;
	User player1User; // White
	User player2User; // Black
	Color turn;
	JungleServer server;
	Timestamp start;
	Timestamp end;
	
	// All code in this class assumes that player1 is white and player2 is black, also player/white always goes first (so the person who invited the other player is player1)
	public ServerGameController(int gameID, JungleClientConnection player1, JungleClientConnection player2, JungleServer server) {
		this.gameID = gameID;
		this.player1 = player1;
		this.player2 = player2;
		this.player1User = player1.getUser();
		this.player2User = player2.getUser();
		this.game = new JungleGame(player1User, player2User);
		this.server = server;
		start = new Timestamp(System.currentTimeMillis());
	}
	public ServerGameController(int gameID, User player1, User player2, String board, JungleServer server) {
		this.gameID = gameID;
		this.player1User = player1;
		this.player2User = player2;
		this.game = new JungleGame(player1User, player2User, board);
		this.server = server;
		start = new Timestamp(System.currentTimeMillis());
	}
	
	// Sends the turn notifications to the users. Maybe in the future this could be used to check if
	// both users are ready to play
	public void startGame() {
		// Player 1 (White) goes first
		this.turn = Color.WHITE;
		sendTurnUpdate(turn);
	}
	
	public void resumeGame(Color turn) {
		this.turn = turn;
		sendTurnUpdate(turn);
	}
	
	public void rejoinGame() {
		sendTurnUpdate(turn);
	}
	
	public void handleMessage(GameMessage message) {
		// Received message should have a matching gameID. 
		// This exception should never be thrown if the server is working properly
		System.out.println("Server game " + message.getGameID() + " received GameMessage of type " + message.getType());
		if(message.getGameID() != gameID)
			throw new RuntimeException("Message for game " + message.getGameID() + " sent to wrong game " + gameID);
		if(message.getType() == GameMessageType.MAKE_MOVE)
			handleMove(message);
		else if(message.getType() == GameMessageType.QUIT_GAME)
			handleQuitGame(message);

	}

	private void handleMove(GameMessage move) {
		Color color = move.getPieceColor();
		String id = move.getPieceID();
		int row = move.getTileRow();
		int col = move.getTileCol();
		// Check to see if it is that colors turn
		if(color == turn) {
			// Currently, we are expecting the client to make sure the move is valid.
			// If the move isn't successful, do nothing to notify the client, and don't pass the move on to the other client
			if(game.makeMove(color, id, row, col)) {
				// Set the turn to the next player
				if(turn == Color.WHITE) turn = Color.BLACK;
				else turn = Color.WHITE;
				sendMove(turn, move);
				sendTurnUpdate(turn);
				// Someone won the game!
				User winner = game.getWinner();
				if(winner != null) {
					gameOver(winner, false);
				}
			}
		}
		server.updateGameInDB(this);
	}
	
	private void handleQuitGame(GameMessage message) {
		if(message.getQuiter().equals(player1User))
			gameOver(player2User, true);
		else
			gameOver(player1User, true);
	}
	
	// Sends turn notifications to the players
	private void sendTurnUpdate(Color turn) {
		GameMessage p1Turn = new GameMessage(gameID, GameMessageType.SET_TURN, turn == Color.WHITE);
		GameMessage p2Turn = new GameMessage(gameID, GameMessageType.SET_TURN, turn == Color.BLACK);
		sendTCP(player1, p1Turn);
		sendTCP(player2, p2Turn);
		
	}
	
	// Sends a move to the specified color
	private void sendMove(Color color, GameMessage move) {
		if(color == Color.WHITE)
			sendTCP(player1, move);
		else
			sendTCP(player2, move);
	}

	// Sends a game over notification to the players, and notifies the server that the game has ended
	private void gameOver(User winner, boolean abandoned) {
		GameMessage gameOver = new GameMessage(gameID, GameMessageType.GAME_OVER, winner);
		sendTCP(player1, gameOver);
		sendTCP(player2, gameOver);
		// Notify the server that the game is over
		end = new Timestamp(System.currentTimeMillis());
		if(winner.equals(player1User))
			server.gameOver(gameID, player1User, player2User, abandoned, start, end);
		else
			server.gameOver(gameID, player2User, player1User, abandoned, start, end);
	}
	
	public int currentTurn() {
		if(turn == Color.WHITE)
			return 1;
		else
			return 2;
	}
	
	public String getBoardRepresentation() {
		return game.getBoard().getBoardRepresentation();
	}
	public User getOpponent(User user) {
		if(user.equals(player1User))
			return player2User;
		else
			return player1User;
	}
	
	public Color getColor(User user) {
		if(user.equals(player1User))
			return Color.WHITE;
		else
			return Color.BLACK;
	}
	
	public void setPlayer1(JungleClientConnection conn) {
		player1 = conn;
	}

	public void setPlayer2(JungleClientConnection conn) {
		player2 = conn;
	}
	
	private void sendTCP(JungleClientConnection conn, GameMessage message) {
		if(conn != null)
			conn.sendTCP(message);
	}
}
