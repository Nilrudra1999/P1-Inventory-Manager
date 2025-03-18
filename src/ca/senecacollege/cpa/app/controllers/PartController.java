/****************************************************************************************************
 * P1-Inventory-Manager - Parts Management Controller
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.controllers;

import ca.senecacollege.cpa.app.models.InHouse;
import ca.senecacollege.cpa.app.models.OutSourced;
import ca.senecacollege.cpa.app.utils.Part;
import ca.senecacollege.cpa.app.utils.SceneName;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.senecacollege.cpa.app.test.Main.getLoader;
import static ca.senecacollege.cpa.app.test.Main.getScene;

/****************************************************************************************************
 * Controller Class: Parts controller - controls the parts scenes and its events
 * This scene can either be loaded in default state (empty and for adding) or can be loading in a
 * special modify state (filled with data for modifying an existing object). The cancel and exit
 * buttons ask for confirmation before wiping the form of all data and either sending the user back
 * or resetting the form page to default state.
 ****************************************************************************************************/
public class PartController implements Initializable {
    @FXML private TextField autoGenTextField, nameTextField, inventoryTextField;
    @FXML private TextField priceTextField, maxTextField, minTextField, partSpecTextField;
    @FXML private RadioButton inHouseRadio, outSourcedRadio;
    @FXML private Label partSpecText;
    @FXML private Text titleText;
    @FXML private Button cancelBtn;

    private int partID;
    private String operationSignal;


    // function disables the id auto generated field, focuses on the name field, and add various listeners
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        autoGenTextField.setDisable(true);
        nameTextField.requestFocus();
        partSpecText.setText("");
        radioButtonClickListener(inHouseRadio);
        radioButtonClickListener(outSourcedRadio);
        textFieldInputChangeListener(partSpecTextField);
        minMaxMetricChecker(maxTextField);
        minMaxMetricChecker(minTextField);
    }



    // Event Handlers --------------------------------------------------------------------------------------------------
    // handles exit by clearing the form of any information and sends user to main screen without saving objects
    public void handleExit(ActionEvent event) {
        if (showConfirmAlert("Exiting will clear any unsaved data, are you sure you want to exit?")) {
            clearAllFields();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(getScene().get(SceneName.MAIN));
            stage.setTitle("Inventory Management Services");
            stage.show();
        }
    }


    // uses a multi-stage error check before adding or updating a part, can do either operation based on signal
    public void handleSave(ActionEvent event) {
        if (anyFieldEmpty() || partSpecText.getText().isEmpty()) {
            showErrorAlert("Either the form Hasn't been filled or manufacturing location hasn't been selected.");
            return;
        }
        Part part = null;
        String partName = nameTextField.getText().trim();
        int partInv = Integer.parseInt(inventoryTextField.getText());
        int partMin = Integer.parseInt(minTextField.getText());
        int partMax = Integer.parseInt(maxTextField.getText());
        if (partInv < partMin || partInv > partMax) {
            showErrorAlert("The inventory amount has been incorrectly entered (greater than min, less than max).");
            Platform.runLater(inventoryTextField::clear);
            return;
        }
        double partPrice = Double.parseDouble(priceTextField.getText());
        if (partSpecText.getText().equals("MachineID")) {
            int partMachineID = Integer.parseInt(partSpecTextField.getText());
            part = new InHouse(partID, partName, partPrice, partInv, partMin, partMax, partMachineID);
        } else if (partSpecText.getText().equals("Company Name")) {
            String partCompany = partSpecTextField.getText().trim();
            part = new OutSourced(partID, partName, partPrice, partInv, partMin, partMax, partCompany);
        }
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        InventoryAppController controller = getLoader().get(SceneName.MAIN).getController();
        if (operationSignal.equals("Add")) { // changes what to do depending on operation signal
            controller.loadControllerAddNewItem(part);
        } else {
            controller.loadControllerUpdateExistingItem(part);
        }
        stage.setScene(getScene().get(SceneName.MAIN));
        stage.setTitle("Inventory Management Services");
        stage.show();
    }


    // clears the form, resets the form back to default settings, so signals to "add", and sets title to "add"
    public void handleCancel(ActionEvent event) {
        if (showConfirmAlert("Are you sure you want to clear parts information?")) {
            clearAllFields();
        }
    }



    // Helper Methods --------------------------------------------------------------------------------------------------
    // gets an id value from any other controller during loading and sets the auto generated id field value
    private void setExistingIDValue(int srcId) {
        this.partID = srcId;
        autoGenTextField.setText(String.valueOf(partID += 1));
    }


    // sets the scene's title such that the page title reflects the operation being performed current
    private void setTitleOfScene(String newTitle) {
        titleText.setText(newTitle);
    }


    // sets the signal page operation, used by the save button handler to understand what operation to perform
    private void setOperationSignal(String signal) {
        this.operationSignal = signal;
    }


    // displays an error alert with changeable text, alert type ERROR
    public void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Part or Part Type Error");
        alert.setContentText(text);
        alert.showAndWait();
    }


    // displays a confirmation alert with changeable text, alert type CONFIRMATION
    public boolean showConfirmAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirm Parts Operation");
        alert.setContentText(text);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }


    // clears the whole form and sets the radio buttons to a de-selected state, resetting focus back to name field
    private void clearAllFields() {
        nameTextField.clear();
        inventoryTextField.clear();
        priceTextField.clear();
        maxTextField.clear();
        minTextField.clear();
        partSpecTextField.clear();
        partSpecText.setText("");
        inHouseRadio.setSelected(false);
        outSourcedRadio.setSelected(false);
        nameTextField.requestFocus();
    }


    // loader function used to set the state of the controller and screen, such that pre-loads the form and modifies
    public void setFormToModifyPart(Part part) {
        clearAllFields();
        this.partID = part.getId();
        autoGenTextField.setText(String.valueOf(part.getId()));
        nameTextField.setText(part.getName());
        inventoryTextField.setText(String.valueOf(part.getStock()));
        priceTextField.setText(String.valueOf(part.getPrice()));
        maxTextField.setText(String.valueOf(part.getMax()));
        minTextField.setText(String.valueOf(part.getMin()));
        if (part instanceof InHouse) {
            inHouseRadio.setSelected(true);
            partSpecTextField.setText(String.valueOf(((InHouse) part).getMachine()));
        } else if (part instanceof OutSourced) {
            outSourcedRadio.setSelected(true);
            partSpecTextField.setText(((OutSourced) part).getCompanyName());
        }
        setTitleOfScene("Modify Part");
        setOperationSignal("Modify");
        cancelBtn.setVisible(false);
        nameTextField.requestFocus();
    }


    // loader function user to set the state of the controller to default and wait for new object creation
    public void setFormToAddPart(int id) {
        clearAllFields();
        setExistingIDValue(id);
        setTitleOfScene("Add Part");
        setOperationSignal("Add");
        cancelBtn.setVisible(true);
        nameTextField.requestFocus();
    }


    // checks all fields to ensure that none of the fields are empty and either one of the radios are selected
    private boolean anyFieldEmpty() {
        boolean textFieldEmpty = false;
        if (nameTextField.getText().isEmpty()) {
            textFieldEmpty = true;
        } else if (inventoryTextField.getText().isEmpty()) {
            textFieldEmpty = true;
        } else if (priceTextField.getText().isEmpty()) {
            textFieldEmpty = true;
        } else if (maxTextField.getText().isEmpty()) {
            textFieldEmpty = true;
        } else if (minTextField.getText().isEmpty()) {
            textFieldEmpty = true;
        } else if (partSpecTextField.getText().isEmpty()) {
            textFieldEmpty = true;
        }
        return textFieldEmpty;
    }



    // Radio Click Listeners -------------------------------------------------------------------------------------------
    private void radioButtonClickListener(RadioButton radioButton) { // changes label when radio buttons are clicked
        radioButton.selectedProperty().addListener((_, _, newVal) -> {
            if (newVal && (inHouseRadio.isSelected() && outSourcedRadio.isSelected())) {
                showErrorAlert("A part can either be in-house or out-sourced, not both.");
                inHouseRadio.setSelected(false);
                outSourcedRadio.setSelected(false);
                partSpecText.setText("");
            } else if (!newVal && (!inHouseRadio.isSelected() && !outSourcedRadio.isSelected())) {
                partSpecText.setText("");
            } else if (newVal && outSourcedRadio.isSelected()) {
                partSpecText.setText("Company Name");
            } else if (newVal && inHouseRadio.isSelected()) {
                partSpecText.setText("MachineID");
            }
            partSpecTextField.setText("");
        });
    }



    // TextField Listeners ---------------------------------------------------------------------------------------------
    private void textFieldInputChangeListener(TextField field) { // changes what patterns are match w/ label change
        partSpecTextField.textProperty().addListener((_, _, newStr) -> {
            Pattern letterPattern = Pattern.compile("^[a-zA-Z\\s]+$");
            Pattern numberPattern = Pattern.compile("^\\d+$");
            Matcher letterMatcher = letterPattern.matcher(newStr);
            Matcher numberMatcher = numberPattern.matcher(newStr);
            if (!field.getText().isEmpty()) {
                if (partSpecText.getText().equals("MachineID") && !numberMatcher.matches()) {
                    showErrorAlert("A Machine ID only accepts numbers.");
                    Platform.runLater(field::clear);
                } else if (partSpecText.getText().equals("Company Name") && !letterMatcher.matches()) {
                    showErrorAlert("A Company Name only accepts letters and no spaces.");
                    Platform.runLater(field::clear);
                }
            }
        });
    }


    private void minMaxMetricChecker(TextField field) { // shows an alert when inventory amounts aren't correct
        field.focusedProperty().addListener((_, _, newVal) -> {
            if (minTextField.getText().isEmpty() || maxTextField.getText().isEmpty()) {
                return;
            }
            int minCnt = Integer.parseInt(minTextField.getText());
            int maxCnt = Integer.parseInt(maxTextField.getText());
            if (!newVal && (minCnt >= maxCnt)) {
                showErrorAlert("Minimum inventory value cannot be greater than or equal to the maximum");
                Platform.runLater(field::clear);
            }
        });
    }
}
