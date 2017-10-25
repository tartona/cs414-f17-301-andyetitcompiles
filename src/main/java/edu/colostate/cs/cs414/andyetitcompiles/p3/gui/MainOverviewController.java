package edu.colostate.cs.cs414.andyetitcompiles.p3.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;


public class MainOverviewController {

	@FXML private GridPane gameBoard;
	@FXML private Button btnFindUser;

	private boolean cellSelected = false;
	private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

	@FXML
	private void initialize() {
		int numCols = 7 ;
		int numRows = 9 ;

		// populating Grid Pane
		for (int i = 0 ; i < numCols ; i++) {
			ColumnConstraints colConstraints = new ColumnConstraints();
			colConstraints.setHgrow(Priority.SOMETIMES);
			gameBoard.getColumnConstraints().add(colConstraints);
		}
		for (int i = 0 ; i < numRows ; i++) {
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setVgrow(Priority.SOMETIMES);
			gameBoard.getRowConstraints().add(rowConstraints);
		}
		for (int i = 0 ; i < numCols ; i++) {
			for (int j = 0; j < numRows; j++) {
				addPane(j, i);
			}
		}

		initBoard(numRows, numCols);
	}

	@FXML
	private void openFindUser() {
		main.showFindUserDialog();
	}

	private void initBoard(int numRows, int numCols) {
		for (int j = 0; j < numRows; j++) {
			for (int i = 0 ; i < numCols ; i++) {
				Label a = new Label();
				a.setText(i+","+j); // set graphic for images
				gameBoard.add((Node)a, i, j);
			}
		}
	}

	private void drawBoard(String[][] board) {
		int cnt = 0;
		for (Node node : gameBoard.getChildren()) {
			if (node instanceof Label) {
				((Label)node).setText(board[cnt/7][cnt%7]);
				cnt++;
			}
		}
	}

	private void addPane(int colIndex, int rowIndex) {
		Pane pane = new Pane();
		pane.setOnMouseClicked(e -> cellClicked(e));
		gameBoard.add(pane, colIndex, rowIndex);
	}

	int tmprow, tmpcol = 0; // delete later
	private void cellClicked(MouseEvent e){
		// testing grid selection
		Node source = (Node)e.getSource() ;
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        if(!cellSelected) {
	        for (Node node : gameBoard.getChildren()) {
				if (node instanceof Label && gameBoard.getColumnIndex(node) == colIndex.intValue() && gameBoard.getRowIndex(node) == rowIndex.intValue()) {
					((Label)node).setText("Selected! "+rowIndex.intValue()+","+colIndex.intValue());
				}
	        }
	        tmprow = rowIndex.intValue();
	        tmpcol = colIndex.intValue();
	        cellSelected = true;
        }else {
        	for (Node node : gameBoard.getChildren()) {
				if (node instanceof Label && gameBoard.getColumnIndex(node) == tmpcol && gameBoard.getRowIndex(node) == tmprow) {
					String tmp[] = ((Label)node).getText().split(" ");
					((Label)node).setText(tmp[1]);
				}
	        }
        	cellSelected = false;
        }
	}
}
