/****************************************************************************************************
 * P1-Inventory-Manager - Login Controller
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.controllers;

import ca.senecacollege.cpa.app.models.Login;
import ca.senecacollege.cpa.app.utils.SceneName;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static ca.senecacollege.cpa.app.test.Main.getScene;

/****************************************************************************************************
 * Controller Class: Login controller - controls the login scene and its events
 * Has a focus change listener for the text fields, checking for empty values, and two button
 * action event listeners for the submit and cancel buttons.
 * Username: name123
 * Password: login2025
 ****************************************************************************************************/
public class LoginController implements Initializable {
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;


    // sets focus listeners on the two text fields for username and password, to highlight them red when empty
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        setFocusListener(usernameTextField);
        setFocusListener(passwordTextField);
    }



    // Event Handlers --------------------------------------------------------------------------------------------------
    // handles the login operation, makes a login object to check if username and password are valid
    public void handleLogin(ActionEvent event) {
        Login creds = new Login();
        if (creds.usernameMatcher(usernameTextField.getText()) && creds.passwordMatcher(passwordTextField.getText())) {
            clearAllFields();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(getScene().get(SceneName.MAIN));
            stage.setTitle("Inventory Management Services");
            stage.show();
        } else {
            clearAllFields();
            showErrorAlert("Either username or password was entered wrong.");
        }
    }


    // clears the form and resets the focus on the username field, after the user confirms the operation
    public void clearForm(ActionEvent event) {
        if (showConfirmAlert("Are you sure you want to clear all login info?")) {
            clearAllFields();
        }
    }



    // Helper Methods --------------------------------------------------------------------------------------------------
    // clears all fields on the screen and resets focus back to the username field
    public void clearAllFields() {
        usernameTextField.clear();
        passwordTextField.clear();
        usernameTextField.requestFocus();
    }


    // shows an error alert with changeable text, with the alert type of ERROR
    public void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login Error");
        alert.setContentText(text);
        alert.showAndWait();
    }


    // shows a confirmation alert with changeable text, with the alert type of CONFIRMATION
    public boolean showConfirmAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirm Login Operation");
        alert.setContentText(text);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }



    // Focus Listener --------------------------------------------------------------------------------------------------
    public void setFocusListener(TextField field) { // if text field is empty after focus-loss, highlights it red
        field.focusedProperty().addListener((_, _, newVal) -> {
            if (!newVal && field.getText().isEmpty()) {
                field.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {
                field.setStyle("");
            }
        });
    }
}
