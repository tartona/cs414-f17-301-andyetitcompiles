package edu.colostate.cs.cs414.andyetitcompiles.p3.gui;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterDialogController {

	@FXML private TextField txtNickname;
	@FXML private TextField txtEmail;
	@FXML private TextField txtPassword;
	@FXML private Button btnRegister;
	@FXML private Button btnCancel;

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
		txtEmail.getText();
		txtPassword.getText();

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
			alert.setHeaderText("Please use another email address.");
			alert.setContentText("Duplicate user!");

			alert.showAndWait();

			return false;
		}
	}

	@FXML
	private void handleRegister() {
		// getting texts from textfields..
		txtNickname.getText();
		txtEmail.getText();
		txtPassword.getText();

		if (isValidInput()) {
			/**
			 * Register the user here
			 */
			dialogStage.close();
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
}