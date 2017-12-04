package edu.colostate.cs.cs414.andyetitcompiles.p3.client;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.esotericsoftware.kryonet.Connection;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleBoard;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JunglePiece;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessage;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.GameMessageType;

public class ClientGameController extends JPanel {
	Connection client;
	JungleClient jClient;
	int gameID;
	JungleGame game;
	User self;
	User opponent;
	Color color; 
	boolean turn;
	// Message queue
	BlockingQueue<GameMessage> messageQueue;
	// UI objects
	BoardUI gameBoardUI;
	JFrame frame;
	JLabel message;
	
	public ClientGameController(int gameID, User self, User opponent, Color color, String board, JungleClient jClient) {
		this.jClient = jClient;
		this.client = jClient.kryoClient;
		this.gameID = gameID;
		this.self = self;
		this.opponent = opponent;
		this.color = color;
		if(color == Color.WHITE)
			this.game = new JungleGame(self, opponent, board);
		else
			this.game = new JungleGame(opponent, self, board);
		// Default to false so the client can't make a move until the server sets their turn
		this.turn = false;
		// Construct the ui
		this.setLayout(new BorderLayout());
		this.setName("Game with " + opponent.getNickname());
		// Create quit button
		JButton quitBtn = new JButton();
		quitBtn.setText("Quit");
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitGame();
			}
		});
		this.add(quitBtn, BorderLayout.PAGE_END);
		// Create a label to display messages
		this.message = new JLabel();
		message.setText("Game starting...");
		this.add(message, BorderLayout.PAGE_START);
		// Finally, add the game board
		gameBoardUI = new BoardUI(game.getJungleTiles(), this);
		this.add(gameBoardUI, BorderLayout.CENTER);
	}
	
	// Lazy fix to get rid of a test setup problem
	public ClientGameController(int gameID, User self, User opponent, Color color, String board, Connection jClient) {
		this.client = jClient;
		this.gameID = gameID;
		this.self = self;
		this.opponent = opponent;
		this.color = color;
		if(color == Color.WHITE)
			this.game = new JungleGame(self, opponent, board);
		else
			this.game = new JungleGame(opponent, self, board);
		// Default to false so the client can't make a move until the server sets their turn
		this.turn = false;
		// Construct the ui
		this.setLayout(new BorderLayout());
		this.setName("Game with " + opponent.getNickname());
		// Create quit button
		JButton quitBtn = new JButton();
		quitBtn.setText("Quit");
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitGame();
			}
		});
		this.add(quitBtn, BorderLayout.PAGE_END);
		// Create a label to display messages
		this.message = new JLabel();
		message.setText("Game starting...");
		this.add(message, BorderLayout.PAGE_START);
		// Finally, add the game board
		gameBoardUI = new BoardUI(game.getJungleTiles(), this);
		this.add(gameBoardUI, BorderLayout.CENTER);
	}
	
	// Used for the demo CLI game
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
	
	// Used by the client to notify the controller of game events
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
		// No need to check the turn, since this is the other players turn
		game.makeMove(piece.getColor(), piece.getID(), message.getTileRow(), message.getTileCol());
		gameBoardUI.update(game.getJungleTiles());
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
			this.message.setText("You won the game!");
			JOptionPane.showMessageDialog(frame, "You have won the game");
		}
		else {
			this.message.setText("You lost the game");
			JOptionPane.showMessageDialog(frame, "You have lost the game");
		}
		jClient.removeGame(gameID);
	}

	private void handleSetTurn(GameMessage message) {
		turn = message.isTurn();
		if(turn) {
			this.message.setText("It is now your turn");
		}
		else {
			this.message.setText("It is now the opponents turn");
		}
	}

	// Used by the demo CLI version of the game
	public void makeMove(String id, String move) {
		JunglePiece jPiece = game.getPiece(color, id);
		JungleTile jTile;
		int currentRow = jPiece.getCurrentRow();
		int currentCol = jPiece.getCurrentCol();
		if(move.equals("up")) {
			jTile = game.getTile(jPiece.getCurrentRow()+1, jPiece.getCurrentCol());
			makeMove(jPiece, currentRow + 1, currentCol);
		}
		else if(move.equals("down")) {
			jTile = game.getTile(jPiece.getCurrentRow()-1, jPiece.getCurrentCol());
			makeMove(jPiece, currentRow - 1, currentCol);
		}
		else if(move.equals("right")) {
			jTile = game.getTile(jPiece.getCurrentRow(), jPiece.getCurrentCol()+1);
			makeMove(jPiece, currentRow, currentCol + 1);
		}
		else if(move.equals("left")) {
			jTile = game.getTile(jPiece.getCurrentRow(), jPiece.getCurrentCol()-1);
			makeMove(jPiece, currentRow, currentCol - 1);
		} 
	}
	
	
	public void makeMove(JunglePiece piece, int row, int col) {
		if(piece.getColor() == color) {
			if(turn) {
				if(game.makeMove(piece.getColor(), piece.getID(), row, col)) {
					GameMessage move = new GameMessage(gameID, GameMessageType.MAKE_MOVE, piece.getColor(), piece.getID(), row, col, self);
					client.sendTCP(move);
					gameBoardUI.update(game.getJungleTiles());
				}
				else {
					this.message.setText("Your move was invalid, try again");
					// notify ui
				}
			}
			else {
				this.message.setText("It's not your turn");
				// notify ui
			}
		}
		else {
			this.message.setText("You can only move " + color + " pieces");
			// notify ui
		}
	}
	
	public void quitGame() {
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
	
	public static void main(String args[]) {

	}
}

