package edu.colostate.cs.cs414.andyetitcompiles.p3.client;

import javax.swing.JPanel;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.TileType;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class BoardUI extends JPanel {
	JungleTile[][] board = new JungleTile[9][7];
	Tile tiles[][] = new Tile[9][7]; 
	ClientGameController controller;
	Tile selectedTile;
	Tile move;

	public BoardUI(JungleTile[][] board, ClientGameController controller) {
		this.board = board;
		this.controller = controller;
		this.setPreferredSize(new Dimension(448, 576));
		setLayout(new GridLayout(9, 7, 0, 0));
		// Fill the layout with the tiles
		for(int row = 0; row < 9; row++) { // Fill row by row
			for(int col = 0; col < 7; col++) {
				Tile tile = new Tile(board[row][col], this);
				tiles[row][col] = tile;
				this.add(tile);
			}
		}
	}
	
	// This is called when one of the tiles is selected
	public void notifySelected(Tile tile) {
		// This is the only tile selected
		if(selectedTile == null) {
			selectedTile = tile;
			tile.setSelected(true);
			// This is when we would do valid move highlighting
		}
		else {
			// Reselected the same tile, deselect it
			if(selectedTile.equals(tile)) {
				tile.setSelected(false);
				selectedTile = null;
				move = null;
			}
			// Selected another tile, try to move there
			else {
				move = tile;
				controller.makeMove(selectedTile.getTile().getCurrentPiece(), move.getTile().getRow(), move.getTile().getCol());
				selectedTile.setSelected(false);
				move.setSelected(false);
				selectedTile = null;
				move = null;
			}
		}
	}
	
	// Updates the tile at row,col with the specified piece. If piece is null, it removes any piece on that tile
	public void update(int row, int col, String piece, String color) {
		Tile tile = tiles[row][col];
		tile.setPiece(piece, color);
	}
	
	// Updates the entire board. This will probably feel slow, so updating individual tiles would be preferred
	public void update(JungleTile[][] board) {
		this.board = board;
		for(int row = 0; row < 9; row++) {
			for(int col = 0; col < 7; col++) {
				if(board[row][col].getCurrentPiece() != null) {
					tiles[row][col].setPiece(board[row][col].getCurrentPiece().getID(), board[row][col].getCurrentPiece().getColor().toString());
				}
				else {
					tiles[row][col].setPiece(null, null);
				}
			}
		}
	}

	public static void main(String args[]) {
		JungleGame game = new JungleGame(null, null);
		JFrame frame = new JFrame();
		frame.setTitle("Game board test");
		frame.add(new BoardUI(game.getJungleTiles(), null));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(448, 576);
		frame.setVisible(true);
	}
}
