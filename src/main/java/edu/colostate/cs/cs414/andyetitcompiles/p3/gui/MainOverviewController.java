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
    	// for accessing root
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
		for (int j = 0; j < numRows; j++) {
			for (int i = 0 ; i < numCols ; i++) {
				addPane(j, i);
				Label a = new Label();
				a.setText("He"); // set graphic for images
				gameBoard.add((Node)a, i, j);
			}
		}
		for (Node node : gameBoard.getChildren()) {
			if (node instanceof Label && gameBoard.getColumnIndex(node) == 3 && gameBoard.getRowIndex(node) == 3) {
				((Label)node).setText("3,3");
			}
		}
	}

	@FXML
	private void openFindUser() {
		// hadnles 'Find User' button click
		main.showFindUserDialog();
	}

	private void addPane(int colIndex, int rowIndex) {
		// add pane to the grid pane
		Pane pane = new Pane();
		pane.setOnMouseClicked(e -> cellClicked(e)); // assigns onClick method
		gameBoard.add(pane, colIndex, rowIndex);
	}

	private void cellClicked(MouseEvent e){
		// when cell is selected
		Node source = (Node)e.getSource() ;
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        System.out.printf("Mouse entered cell [%d, %d]%n", colIndex.intValue(), rowIndex.intValue());
        cellSelected = true;
	}
}
