package edu.colostate.cs.cs414.andyetitcompiles.p3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FindUserDialogController {

	@FXML private TextField txtNickname;
	@FXML private Button btnViewUserP;
	@FXML private Button btnSendInv;

	private Stage dialogStage;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	private boolean isValidInput() {
		// getting texts from textfields..
		txtNickname.getText();

		boolean test = true;
		// some test..
		if (test) {
			/**
			 * check for duplicate email address
			 */
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid User Information");
			alert.setHeaderText("Please check the nickname.");
			alert.setContentText("Invalid user!");

			alert.showAndWait();

			return false;
		}
	}

	@FXML
	private void handleSendInv() {
		dialogStage.close();
	}

	@FXML
	private void handleViewUserP() {
		dialogStage.close();
	}
}