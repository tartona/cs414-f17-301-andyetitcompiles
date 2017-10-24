package edu.colostate.cs.cs414.andyetitcompiles.p3.client;

import java.awt.Color;

import com.esotericsoftware.kryonet.Connection;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessage;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessageType;

public class ClientGameController {
	Connection client;
	int gameID;
	JungleGame game;
	User self;
	User opponent;
	Color color; 
	boolean turn;
	
	public ClientGameController(int gameID, User self, User opponent, Color color, Connection client) {
		this.client = client;
		this.gameID = gameID;
		this.self = self;
		this.opponent = opponent;
		this.color = color;
		if(color == Color.WHITE)
			this.game = new JungleGame(self, opponent);
		else
			this.game = new JungleGame(opponent, self);
		// Default to false so the client can't make a move until the server sets their turn
		this.turn = false;
	}
	
	public void handleMessage(GameMessage message) {
		if(message.getGameID() != gameID)
			throw new IllegalArgumentException("Message for wrong game " + message.getGameID() + " received. Should be " + gameID);
		if(message.getType() == GameMessageType.UPDATE) {
			handleUpdate(message);
		}
		else if(message.getType() == GameMessageType.SET_TURN) {
			handleSetTurn(message);
		}
		else if(message.getType() == GameMessageType.GAME_OVER) {
			handleGameOver(message);
		}
		else
			throw new IllegalArgumentException("Invalid message type received by client game controller " + gameID);
	}

	private void handleGameOver(GameMessage message) {
		if(message.getWinner().equals(self)) {
			System.out.println("You won the game!");
			// Notify ui
		}
		else {
			System.out.println("You lost the game");
			// Notify ui
		}
		// Let the client know it can get rid of this instance
	}

	private void handleSetTurn(GameMessage message) {
		turn = message.isTurn();
		// Notify ui
	}

	private void handleUpdate(GameMessage message) {
		game.updateBoard(message.getBoard());
		// notify ui
	}
	
	// All of the following methods are used by the client to interact with the game. 
	// They will be triggered by the ui, however that ends up being implemented
	
	public void makeMove(JunglePiece piece, JungleTile tile) {
		if(piece.getColor() == color) {
			if(turn) {
				if(game.makeMove(piece, tile)) {
					System.out.println("Move successful");
					GameMessage move = new GameMessage(gameID, GameMessageType.MAKE_MOVE, piece, tile, self);
					client.sendTCP(move);
					// Notify ui, maybe. The server sends a game state update to both players when one makes a move, so maybe we shouldn't update the ui twice.
				}
				else {
					System.out.println("Your move was invalid, try again");
					// notify ui
				}
			}
			else {
				System.out.println("It's not your turn");
				// notify ui
			}
		}
		else {
			System.out.println("You can only move" + color + " peices");
			// notify ui
		}
	}
	
	public void guitGame() {
		GameMessage quit = new GameMessage(gameID, GameMessageType.QUIT_GAME, self);
		client.sendTCP(quit);
	}
}

