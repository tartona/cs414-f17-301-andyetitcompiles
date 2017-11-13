package edu.colostate.cs.cs414.andyetitcompiles.p3.client;

import javax.swing.JPanel;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleGame;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.TileType;

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
		setLayout(new GridLayout(9, 7, 0, 0));
		// Fill the layout with the tiles
		for(int row = 0; row < 9; row++) { // Fill row by row
			for(int col = 0; col < 7; col++) {
				Tile tile = new Tile(board[row][col], this);
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
				String piece = selectedTile.getPiece();
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
