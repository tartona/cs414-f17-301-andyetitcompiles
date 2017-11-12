package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

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
	
	// All code in this class assumes that player1 is white and player2 is black, also player/white always goes first (so the person who invited the other player is player1)
	public ServerGameController(int gameID, JungleClientConnection player1, JungleClientConnection player2) {
		this.gameID = gameID;
		this.player1 = player1;
		this.player2 = player2;
		this.player1User = player1.getUser();
		this.player2User = player2.getUser();
		this.game = new JungleGame(player1User, player2User);
	}
	
	// Sends the turn notifications to the users. Maybe in the future this could be used to check if
	// both users are ready to play
	public void startGame() {
		// Player 1 (White) goes first
		this.turn = Color.WHITE;
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
		JunglePiece piece = game.getPiece(move.getPieceColor(), move.getPieceID());
		int row = move.getTileRow();
		int col = move.getTileCol();
		// Check to see if it is that colors turn
		if(piece.getColor() == turn) {
			// Currently, we are expecting the client to make sure the move is valid.
			// If the move isn't successful, do nothing to notify the client, and don't pass the move on to the other client
			if(game.makeMove(piece, row, col)) {
				// Set the turn to the next player
				if(turn == Color.WHITE) turn = Color.BLACK;
				else turn = Color.WHITE;
				sendMove(turn, move);
				sendTurnUpdate(turn);
				// Someone won the game!
				User winner = game.getWinner();
				if(winner != null) {
					gameOver(winner);
				}
			}
		}
	}
	
	private void handleQuitGame(GameMessage message) {
		if(message.getQuiter().equals(player1User))
			gameOver(player2User);
		else
			gameOver(player1User);
	}
	
	// Sends turn notifications to the players
	private void sendTurnUpdate(Color turn) {
		GameMessage p1Turn = new GameMessage(gameID, GameMessageType.SET_TURN, turn == Color.WHITE);
		GameMessage p2Turn = new GameMessage(gameID, GameMessageType.SET_TURN, turn == Color.BLACK);
		player1.sendTCP(p1Turn);
		player2.sendTCP(p2Turn);
		
	}
	
	// Sends a move to the specified color
	private void sendMove(Color color, GameMessage move) {
		if(color == Color.WHITE)
			player1.sendTCP(move);
		else
			player2.sendTCP(move);
	}

	// Sends a game over notification to the players, and notifies the server that the game has ended
	private void gameOver(User winner) {
		GameMessage gameOver = new GameMessage(gameID, GameMessageType.GAME_OVER, winner);
		player1.sendTCP(gameOver);
		player2.sendTCP(gameOver);
		// Now we have to tell the server that the game is over, so it can remove the game instance and create game records for each user
	}
}
