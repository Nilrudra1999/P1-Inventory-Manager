/****************************************************************************************************
 * P1-Inventory-Manager - Products Management Controller
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.controllers;

import ca.senecacollege.cpa.app.models.Product;
import ca.senecacollege.cpa.app.utils.Part;
import ca.senecacollege.cpa.app.utils.SceneName;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.senecacollege.cpa.app.test.Main.getLoader;
import static ca.senecacollege.cpa.app.test.Main.getScene;

/****************************************************************************************************
 * Controller Class: Products controller - controls the products scenes and its events
 * Ensures that the price of any product is greater than the sum of its parts price, and ensures
 * that the inventory is between the min and max amounts. Additionally, it dynamically adds or
 * removes items, dynamically adds or subtracts from the price from removing or adding items, and
 * allows users to search through the list of parts using either a name or id.
 ****************************************************************************************************/
public class ProductController implements Initializable {
    @FXML private TextField autoGenTextField, nameTextField, inventoryTextField;
    @FXML private TextField priceTextField, maxTextField, minTextField, partSearchTextField;
    @FXML private TableView<Part> addPartTable, deletePartTable;
    @FXML private TableColumn<Part, Integer> partidCol01, partidCol02;
    @FXML private TableColumn<Part, String> partNameCol01, partNameCol02;
    @FXML private TableColumn<Part, Integer> partInvCol01, partInvCol02;
    @FXML private TableColumn<Part, Double> partCostCol01, partCostCol02;
    @FXML private Text titleText;
    @FXML private Button cancelBtn;

    ObservableList<Part> parts = FXCollections.observableArrayList();
    ObservableList<Part> productParts = FXCollections.observableArrayList();
    private int productID;
    private String operationSignal;


    // sets the cell values for the table views and displays the parts, formats price columns, and sets focus
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        partidCol01.setCellValueFactory(new PropertyValueFactory<>("id"));
        partidCol02.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol01.setCellValueFactory(new PropertyValueFactory<>("name"));
        partNameCol02.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInvCol01.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partInvCol02.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partCostCol01.setCellValueFactory(new PropertyValueFactory<>("price"));
        partCostCol02.setCellValueFactory(new PropertyValueFactory<>("price"));
        formatPriceColumn(partCostCol01);
        formatPriceColumn(partCostCol02);
        minMaxMetricChecker(minTextField);
        minMaxMetricChecker(maxTextField);
        searchPartsTableListener(partSearchTextField);
        autoGenTextField.setDisable(true);
        nameTextField.requestFocus();
    }



    // Event Handlers --------------------------------------------------------------------------------------------------
    // exit and redirect user to the main screen, after confirmation is received and clears all prior data
    public void handleExit(ActionEvent event) {
        if (showConfirmAlert("Exiting will clear any unsaved data, are you sure you want to exit?")) {
            clearAllFields();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(getScene().get(SceneName.MAIN));
            stage.setTitle("Inventory Management Services");
            stage.show();
        }
    }


    // saves data currently on form and either adds new product or modifies existing depending on operation signal
    public void handleSave(ActionEvent event) {
        if (anyFieldEmpty()) { // exception guard
            showErrorAlert("Some part of the form hasn't been filled, go back and check inputs.");
            return;
        }
        Product product;
        String name = nameTextField.getText();
        int productInv = Integer.parseInt(inventoryTextField.getText());
        int min = Integer.parseInt(minTextField.getText());
        int max = Integer.parseInt(maxTextField.getText());
        if (productInv < min || productInv > max) { // inventory error guard
            showErrorAlert("The inventory amount has been incorrectly entered (greater than min, less than max).");
            Platform.runLater(inventoryTextField::clear);
            return;
        }
        double proPrice = Double.parseDouble(priceTextField.getText());
        double allPartsPrice = 0.0;
        int id = Integer.parseInt(autoGenTextField.getText());
        product = new Product(id, name, proPrice, productInv, min, max);
        for (Part part : deletePartTable.getItems()) {
            allPartsPrice += part.getPrice();
            product.addAssociatedPart(part);
        }
        if (product.getPrice() <= allPartsPrice) {
            product.setPrice(allPartsPrice + 100);
            showInfoAlert("The price of products was unrealistic, so $100 was temporarily set as it's price.");
        }
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        InventoryAppController controller = getLoader().get(SceneName.MAIN).getController();
        if (operationSignal.equals("Add")) { // changes what to do depending on operation signal
            controller.loadControllerAddNewItem(product);
        } else {
            controller.loadControllerUpdateExistingItem(product);
        }
        stage.setScene(getScene().get(SceneName.MAIN));
        stage.setTitle("Inventory Management Services");
        stage.show();
    }


    // clears the form and all data when this function is enabled within this screen, after confirmation
    public void handleCancel(ActionEvent event) {
        if (showConfirmAlert("Are you sure you want to clear all Products information?")) {
            clearAllFields();
        }
    }


    // adds parts to a product from a parts table and the added parts are displayed on the added parts table
    public void handleAddingParts(ActionEvent event) {
        Part selectPart = addPartTable.getSelectionModel().getSelectedItem();
        if (selectPart == null) {
            showErrorAlert("Please select a part that you want to add to the product.");
        } else if (showConfirmAlert("Are you sure you want to Add this part?")) {
            productParts.add(selectPart);
            deletePartTable.setItems(productParts);
            if (priceTextField.getText().isEmpty()) {
                priceTextField.setText(String.valueOf(0.0));
            }
            double totalPrice = Double.parseDouble(priceTextField.getText());
            totalPrice += selectPart.getPrice();
            priceTextField.setText(String.valueOf(totalPrice));
        }
        addPartTable.getSelectionModel().clearSelection();
    }


    // removes parts from the associated parts table so that list of parts belonging to a product can be modified
    public void handleRemovingParts(ActionEvent event) {
        Part selectPart = deletePartTable.getSelectionModel().getSelectedItem();
        if (selectPart == null) {
            showErrorAlert("Please select an associated part that you want to delete from the product.");
        } else if (showConfirmAlert("Are you sure you want to delete this part, from this product?")) {
            productParts.remove(selectPart);
            deletePartTable.setItems(productParts);
            if (priceTextField.getText().isEmpty()) {
                priceTextField.setText(String.valueOf(0.0));
            }
            double totalPrice = Double.parseDouble(priceTextField.getText());
            totalPrice -= selectPart.getPrice();
            priceTextField.setText(String.valueOf(totalPrice));
        }
        deletePartTable.getSelectionModel().clearSelection();
    }



    // Helper Methods --------------------------------------------------------------------------------------------------
    // gets an id value from any other controller during loading and sets the auto generated id field value
    private void setExistingIDValue(int srcId) {
        this.productID = srcId;
        autoGenTextField.setText(String.valueOf(productID += 1));
    }


    // sets the scene's title such that the page title reflects the operation being performed currently
    private void setTitleOfScene(String newTitle) {
        titleText.setText(newTitle);
    }


    // sets the signal for the page operation, used by the save button handler to perform the correct operation
    private void setOperationSignal(String signal) {
        this.operationSignal = signal;
    }


    // shows product or product type error alerts using changeable text, alert type ERROR
    public void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Product or Product Type Error");
        alert.setContentText(text);
        alert.showAndWait();
    }


    // shows only confirmation alerts with changeable text, alert type CONFIRMATION
    public boolean showConfirmAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirm Item Operation");
        alert.setContentText(text);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }


    // shows only information alerts with changeable text, alert type of INFORMATION
    public void showInfoAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Inventory Information");
        alert.setContentText(text);
        alert.showAndWait();
    }


    // loader method which loads the page in default settings, allowing the user to have full functionality
    public void setFormToAddProduct(int srcID, ObservableList<Part> srcParts) {
        clearAllFields();
        this.parts = srcParts;
        addPartTable.setItems(parts);
        deletePartTable.setItems(productParts);
        setExistingIDValue(srcID);
        setTitleOfScene("Add Product");
        setOperationSignal("Add");
        cancelBtn.setVisible(true);
        nameTextField.requestFocus();
    }


    // loader method loads the page with modify settings, fills the form, and certain functions are disabled
    public void setFormToModifyProduct(Product srcProduct, ObservableList<Part> srcParts) {
        clearAllFields();
        this.parts = srcParts;
        addPartTable.setItems(parts);
        this.productParts = srcProduct.getAllAssociatedParts();
        deletePartTable.setItems(productParts);
        this.productID = srcProduct.getId();
        autoGenTextField.setText(String.valueOf(srcProduct.getId()));
        nameTextField.setText(srcProduct.getName());
        inventoryTextField.setText(String.valueOf(srcProduct.getStock()));
        priceTextField.setText(String.valueOf(srcProduct.getPrice()));
        maxTextField.setText(String.valueOf(srcProduct.getMax()));
        minTextField.setText(String.valueOf(srcProduct.getMin()));
        deletePartTable.setItems(srcProduct.getAllAssociatedParts());
        setTitleOfScene("Modify Product");
        setOperationSignal("Modify");
        cancelBtn.setVisible(false);
        nameTextField.requestFocus();
    }


    // sets all fields to empty and resets the focus back onto the name field, then resets the tableviews as well
    private void clearAllFields() {
        nameTextField.clear();
        inventoryTextField.clear();
        priceTextField.clear();
        maxTextField.clear();
        minTextField.clear();
        productParts.clear();
        deletePartTable.setItems(productParts);
        addPartTable.setItems(parts);
        nameTextField.requestFocus();
    }


    // checks all fields to ensure that none of the fields are empty, used with error checks
    private boolean anyFieldEmpty() {
        boolean isAnyEmpty = false;
        if (nameTextField.getText().isEmpty()) {
            isAnyEmpty = true;
        } else if (inventoryTextField.getText().isEmpty()) {
            isAnyEmpty = true;
        } else if (priceTextField.getText().isEmpty()) {
            isAnyEmpty = true;
        } else if (maxTextField.getText().isEmpty()) {
            isAnyEmpty = true;
        } else if (minTextField.getText().isEmpty()) {
            isAnyEmpty = true;
        }
        return isAnyEmpty;
    }



    // TextField Listeners ---------------------------------------------------------------------------------------------
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


    private void searchPartsTableListener(TextField field) { // reloads parts table with filtered items when triggered
        field.textProperty().addListener((_, _, newStr) -> { // filters both letters and number but separately
            ObservableList<Part> filteredParts = FXCollections.observableArrayList();
            Pattern errorPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).+$");
            Pattern letterPattern = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");
            Pattern numberPattern = Pattern.compile("^\\d+$");
            Matcher errorMatcher = errorPattern.matcher(newStr);
            Matcher letterMatcher = letterPattern.matcher(newStr);
            Matcher numberMatcher = numberPattern.matcher(newStr);
            if (newStr.isEmpty()) { // empty searchBar -> loads default table
                addPartTable.setItems(parts);
            } else if (errorMatcher.matches()) {
                showErrorAlert("Cannot enter letters and numbers together.");
                addPartTable.setItems(parts);
            } else if (letterMatcher.matches()) {
                for (Part part: parts) {
                    if (part.getName().toLowerCase().contains(newStr.toLowerCase())) {
                        filteredParts.add(part);
                    }
                }
                addPartTable.setItems(filteredParts);
            } else if (numberMatcher.matches()) {
                for (Part part: parts) {
                    if (part.getId() == Integer.parseInt(newStr)) {
                        filteredParts.add(part);
                        break;
                    }
                }
                addPartTable.setItems(filteredParts);
            }
        });
    }



    // TableView Formater ----------------------------------------------------------------------------------------------
    private <T> void formatPriceColumn(TableColumn<T, Double> column) { // formats price columns for 2 decimal places
        column.setCellFactory(doubleTableColumn -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) { // stops rows from setting "nu"
                    setText(null);
                } else {
                    setText(String.format("%.2f", price));
                }
            }
        });
    }
}
