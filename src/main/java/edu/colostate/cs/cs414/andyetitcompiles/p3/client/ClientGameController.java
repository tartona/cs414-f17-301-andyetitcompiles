package edu.colostate.cs.cs414.andyetitcompiles.p3.client;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import com.esotericsoftware.kryonet.Connection;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleBoard;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessage;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessageType;

public class ClientGameController implements Runnable{
	Connection client;
	int gameID;
	JungleGame game;
	User self;
	User opponent;
	Color color; 
	boolean turn;
	// Message queue
	BlockingQueue<GameMessage> messageQueue;
	// UI objects
	GameConsoleUI gameConsole;
	JFrame frame;
	
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
		gameConsole = new GameConsoleUI(this);
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(gameConsole);
		frame.setSize(700, 500);
		frame.setVisible(true);
		gameConsole.updateConsole(boardRepresentation());
	}
	
	public void handleUserInput(String message) {
		if(message.equals("quit")) {
			quitGame();
		}
		else if(message.split(" ")[0].equals("move")) {
			String piece = message.split(" ")[1];
			String move = message.split(" ")[2];
			makeMove(piece, move);
		}
	}
	
	public void handleMessage(GameMessage message) {
		if(message.getGameID() != gameID)
			throw new IllegalArgumentException("Message for wrong game " + message.getGameID() + " received. Should be " + gameID);
		if(message.getType() == GameMessageType.SET_TURN) {
			handleSetTurn(message);
		}
		else if(message.getType() == GameMessageType.GAME_OVER) {
			handleGameOver(message);
		}
		else if(message.getType() == GameMessageType.MAKE_MOVE) {
			handleMakeMove(message);
		}
		else
			throw new IllegalArgumentException("Invalid message type" + message.getType() + " received by client game controller " + gameID);
	}
	
	// Handles when the client receives a move from the server, representing the other players move
	private void handleMakeMove(GameMessage message) {
		JunglePiece piece = game.getPiece(message.getPieceColor(), message.getPieceID());
		JungleTile tile = game.getTile(message.getTileRow(), message.getTileCol());
		// No need to check the turn, since this is the other players turn
		game.makeMove(piece, tile);
		gameConsole.updateConsole(boardRepresentation());
	}
	
	private String boardRepresentation() {
		String board = "";
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 7; j++){
				if(game.getBoard().getTiles()[i][j].getCurrentPiece() == null) 
					board = board + game.getBoard().getTiles()[i][j].getType() + "\t";
				else
					board = board + game.getBoard().getTiles()[i][j].getCurrentPiece().toString() + " \t";

			}
			board = board + "\n";
		}
		return board;
	}

	private void handleGameOver(GameMessage message) {
		if(message.getWinner().equals(self)) {
			gameConsole.updateConsole("You won the game!");
		}
		else {
			gameConsole.updateConsole("You lost the game");
		}
		// Let the client know it can get rid of this instance
	}

	private void handleSetTurn(GameMessage message) {
		turn = message.isTurn();
		if(turn) {
			gameConsole.updateConsole("It is now your turn");
		}
		else {
			gameConsole.updateConsole("It is now the opponents turn");
		}
	}

	public void makeMove(String piece, String move) {
		JunglePiece jPiece = game.getPiece(color, piece);
		JungleTile jTile;
		if(move.equals("up")) {
			jTile = game.getTile(jPiece.getCurrentRow()+1, jPiece.getCurrentCol());
			makeMove(jPiece, jTile);
		}
		else if(move.equals("down")) {
			jTile = game.getTile(jPiece.getCurrentRow()-1, jPiece.getCurrentCol());
			makeMove(jPiece, jTile);
		}
		else if(move.equals("right")) {
			jTile = game.getTile(jPiece.getCurrentRow(), jPiece.getCurrentCol()+1);
			makeMove(jPiece, jTile);
		}
		else if(move.equals("left")) {
			jTile = game.getTile(jPiece.getCurrentRow(), jPiece.getCurrentCol()-1);
			makeMove(jPiece, jTile);
		}
	}
	public void makeMove(JunglePiece piece, JungleTile tile) {
		if(piece.getColor() == color) {
			if(turn) {
				if(game.makeMove(piece, tile)) {
					gameConsole.updateConsole("Move successful");
					GameMessage move = new GameMessage(gameID, GameMessageType.MAKE_MOVE, piece.getColor(), piece.getID(), tile.getRow(), tile.getCol(), self);
					client.sendTCP(move);
					gameConsole.updateConsole(boardRepresentation());
				}
				else {
					gameConsole.updateConsole("Your move was invalid, try again");
					// notify ui
				}
			}
			else {
				gameConsole.updateConsole("It's not your turn");
				// notify ui
			}
		}
		else {
			gameConsole.updateConsole("You can only move" + color + " peices");
			// notify ui
		}
	}
	
	public void quitGame() {
		gameConsole.updateConsole("Quitting game...");
		GameMessage quit = new GameMessage(gameID, GameMessageType.QUIT_GAME, self);
		client.sendTCP(quit);
	}
	
	// Returns the game. For testing, or maybe for the ui to use
	public JungleGame getGame() {
		return game;
	}
	
	// Also for testing
	public boolean getTurn() {
		return turn;
	}
	
	public void setTurn(boolean b) {
		turn = b;
	}

	public JungleBoard getBoard() {
		return game.getBoard();
	}

	// Run the game instance in a JFrame
	@Override
	public void run() {
		
	}
}

