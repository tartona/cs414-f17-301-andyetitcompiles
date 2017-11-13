package edu.colostate.cs.cs414.andyetitcompiles.p3.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.JungleTile;

public class Tile extends JPanel {
	JungleTile tile;
	BufferedImage background;
	BufferedImage piece;
	JButton button;
	boolean selected;

	/**
	 * Create the panel.
	 */
	public Tile(JungleTile tile, BoardUI board) {
		this.tile = tile;
		piece = null;
		selected = false;
		this.setSize(64,64);
		// Load the background image
		switch(tile.getType()) {
		case RIVER:
			background = getImage("watertile.png");
			break;
		case TRAP:
			background = getImage("traptile.png");
			break;
		case NORMAL:
			background = getImage("regulartile.png");
			break;
		case B_DEN:
		case W_DEN:
			background = getImage("dentile.png");
			break;
		}
		// Load the piece image if there is one
		if(tile.getCurrentPiece() != null) {
			String color;
			if(tile.getCurrentPiece().getColor() == Color.BLACK)
				color = "black";
			else
				color = "white";
			switch(tile.getCurrentPiece().getID()) {
			case "rat":
				piece = getImage(color+"rat.png");
				break;
			case "cat":
				piece = getImage(color+"cat.png");
				break;
			case "dog":
				piece = getImage(color+"dog.png");
				break;
			case "wolf":
				piece = getImage(color+"wolf.png");
				break;
			case "leopard":
				piece = getImage(color+"leopard.png");
				break;
			case "tiger":
				piece = getImage(color+"tiger.png");
				break;
			case "lion":
				piece = getImage(color+"lion.png");
				break;
			case "elephant":
				piece = getImage(color+"elephant.png");
				break;
			}
		}
		// Create the button
		button = new JButton();
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setPreferredSize(new Dimension(64, 64));
		Tile thisTile = this;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.notifySelected(thisTile);
			}
		});
		this.add(button);
	} 
	
	public boolean selected() {
		return selected;
	}
	
	public void setSelected(boolean b) {
		selected = b;
		this.repaint();
	}
	
	public JungleTile getTile() {
		return tile;
	}
	
	public String getColor() {
		if(tile.getCurrentPiece().getColor() == Color.WHITE)
			return "white";
		else
			return "black";
	}
	
	public String getPiece() {
		return tile.getCurrentPiece().getID();
	}
	
	public void setPiece(String id, String color) {
		if(id != null) {
			piece = getImage(color+id+".png");
			this.repaint();
		}
		else {
			piece = null;
			this.repaint();
		}
	}
	
	private BufferedImage getImage(String filename) {
		try {
			return ImageIO.read(getClass().getClassLoader().getResource(filename));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		if(piece != null) {
			g.drawImage(piece, 0, 0, this);
		}
		if(selected) {
			java.awt.Color green = new java.awt.Color(0, 255, 0, 127);
			g.setColor(green);
			g.fillRect(0, 0, 64, 64);
		}
	}
}
