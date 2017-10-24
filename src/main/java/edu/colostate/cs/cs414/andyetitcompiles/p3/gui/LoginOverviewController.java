package edu.colostate.cs.cs414.andyetitcompiles.p3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginOverviewController {

	@FXML private TextField txtEmail;
	@FXML private TextField txtPassword;
	@FXML private Button btnLogin;
	@FXML private Button btnRegister;

	private Main main;

	public LoginOverviewController() {}

	@FXML
	private void initialize() {}

    @FXML
    private void openRegister() {
    	// open Register Screen in a new dialog
    	main.showRegisterDialog();
    }

    public void setMain(Main main) {
    	// For accessing root page
        this.main = main;
    }

    private boolean isValidInput() {
    	// getting texts from textfields
    	txtEmail.getText();
    	txtPassword.getText();

    	boolean test = true;
    	// some test
    	if (test) {
    		/**
    		 * check for duplicate email address
    		 */
    		return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner((Stage) btnLogin.getScene().getWindow());
            alert.setTitle("Invalid User Information");
            alert.setHeaderText("Please revise username/password.");
            alert.setContentText("Invalid user!");
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    private void handleLogin() {
    	// getting texts from textfields..
    	txtEmail.getText();
    	txtPassword.getText();

        if (isValidInput()) {
        	// move to main
        	main.showMainOverview();
        }
    }
}