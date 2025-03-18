/****************************************************************************************************
 * P1-Inventory-Manager - Inventory App Main Controller
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.controllers;

import ca.senecacollege.cpa.app.models.InHouse;
import ca.senecacollege.cpa.app.models.Inventory;
import ca.senecacollege.cpa.app.models.OutSourced;
import ca.senecacollege.cpa.app.models.Product;
import ca.senecacollege.cpa.app.utils.Part;
import ca.senecacollege.cpa.app.utils.SceneName;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.senecacollege.cpa.app.test.Main.getLoader;
import static ca.senecacollege.cpa.app.test.Main.getScene;

/****************************************************************************************************
 * Controller Class: Inventory App Main controller - controls the main scene and its events
 * Has error alerts, confirmation alerts, and information alerts for various user interactions. Also
 * provides the user with a dynamic way of filtering the list of parts and product using a search,
 * which automatically updates the tableview as user enters things. Inventory is kept as a static
 * class variable, along with product and part IDs for app wide data consistencies.
 ****************************************************************************************************/
public class InventoryAppController implements Initializable {
    @FXML private TextField productSearchTextField, partSearchTextField;
    @FXML private TableView<Product> productTable;
    @FXML private TableView<Part> partTable;
    @FXML private TableColumn<Product, Integer> productIdCol, productInvCol;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, Double> productPriceCol;
    @FXML private TableColumn<Part, Integer> partIdCol, partInvCol;
    @FXML private TableColumn<Part, String> partNameCol;
    @FXML private TableColumn<Part, Double> partPriceCol;

    private static Inventory inv = new Inventory();
    private static int productID = 0;
    private static int partID = 0;


    // sets the cell values of the tableview, formats specific column values, and loads sample data
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        formatPriceColumn(productPriceCol);
        searchProductsTableListener(productSearchTextField);
        partIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        formatPriceColumn(partPriceCol);
        searchPartsTableListener(partSearchTextField);
        loadSampleData(10, 5); // currently loading 10 products and 5 parts
        productTable.setItems(inv.getAllProducts());
        partTable.setItems(inv.getAllParts());
    }



    // Event Handlers --------------------------------------------------------------------------------------------------
    // exits the main app screen and takes the user back to the login screen, after confirmation
    public void exitMainPage(ActionEvent event) {
        if (showConfirmAlert("Are you sure you want to leave?")) {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(getScene().get(SceneName.LOGIN));
            stage.setTitle("Login");
            stage.show();
        }
    }


    // redirects users to the product page, setting the product controller to add state before transferring
    public void handleAddProduct(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ProductController controller = getLoader().get(SceneName.PRODUCT).getController();
        controller.setFormToAddProduct(productID, inv.getAllParts());
        stage.setScene(getScene().get(SceneName.PRODUCT));
        stage.setTitle("Add New Product");
        stage.show();
    }


    // redirects users to the product page, but sets the controller to modify a product, changing the screen state
    public void handleModifyProduct(ActionEvent event) {
        Product selectProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectProduct == null) { // exception guard statement
            showErrorAlert("Please first select a product you want to modify.");
        } else if (showConfirmAlert("Are you sure you want to Modify this product?")) {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            ProductController controller = getLoader().get(SceneName.PRODUCT).getController();
            controller.setFormToModifyProduct(selectProduct, inv.getAllParts());
            stage.setScene(getScene().get(SceneName.PRODUCT));
            stage.setTitle("Modify Existing Product");
            stage.show();
        }
        productTable.getSelectionModel().clearSelection();
    }


    // prompts users to select product before deleting, only deletes a product when it has no parts added
    public void handleDeleteProduct(ActionEvent event) {
        Product selectProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectProduct == null) { // exception guard statement
            showErrorAlert("Please first select a product you want to delete.");
        } else if (showConfirmAlert("Are you sure you want to delete this product?")) {
            ObservableList<Part> parts = FXCollections.observableArrayList(selectProduct.getAllAssociatedParts());
            if (!parts.isEmpty()) { // ensures products with parts aren't deleted
                showErrorAlert("This product still has parts, please modify the product to delete all parts.");
                productTable.getSelectionModel().clearSelection();
                return;
            }
            inv.deleteProduct(selectProduct);
            productTable.setItems(inv.getAllProducts());
        }
        productTable.getSelectionModel().clearSelection();
    }


    // directs user to part screen, transfers the current partID value, and signals the controller to "add"
    public void handleAddPart(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        PartController controller = getLoader().get(SceneName.PART).getController();
        controller.setFormToAddPart(partID);
        stage.setScene(getScene().get(SceneName.PART));
        stage.setTitle("Add New Part");
        stage.show();
    }


    // directs users to the part screen, but signals the controller to "modify", only when a part is selected
    public void handleModifyPart(ActionEvent event) {
        Part selectedPart = partTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) {
            showErrorAlert("Please select a part which you want to modify.");
        } else if (showConfirmAlert("Are you sure you want to Modify this part?")) {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            PartController controller = getLoader().get(SceneName.PART).getController();
            controller.setFormToModifyPart(selectedPart);
            stage.setScene(getScene().get(SceneName.PART));
            stage.setTitle("Modify Existing Part");
            stage.show();
        }
        partTable.getSelectionModel().clearSelection();
    }


    // deletes a part, only if a part is selected, and confirms the deletion before performing it
    public void handleDeletePart(ActionEvent event) {
        Part selectedPart = partTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) { // exception guard statement
            showErrorAlert("Please first select a part you want to delete.");
        } else if (showConfirmAlert("Are you sure you want to delete this part?")) {
            inv.deletePart(selectedPart);
            partTable.setItems(inv.getAllParts());
        }
        partTable.getSelectionModel().clearSelection();
    }



    // Helper Methods --------------------------------------------------------------------------------------------------
    // shows only error alerts with changeable text, alert type of ERROR
    public void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Inventory or Item Error");
        alert.setContentText(text);
        alert.showAndWait();
    }


    // shows only information alerts with changeable text, alert type of INFORMATION
    public void showInfoAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Inventory Information");
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


    // loads sample data from a pre-determined set of values, somewhat randomly adds parts, sometimes doesn't
    private void loadSampleData(int numOfProducts, int numOfParts) {
        String[] parts = {"Hex Nut", "Ball bearing", "Axles", "Coil springs", "helical gears"};
        String[] products = {"Milling Machine", "Drill Press", "CNC Machine", "Motor", "6-Speed Auto-GearBox"};
        String[] companys = {"Seneca Polytechnic", "NuBuild Inc", "BNC Ltd"};
        Random random = new Random(); // randomizer object for providing variety
        for (int i = 0; i < numOfProducts; i++) {
            int pIn = random.nextInt(parts.length);
            int proIn = random.nextInt(products.length);
            int nIn = random.nextInt(companys.length);
            double price = random.nextDouble() * 150.0;
            int min = random.nextInt(21);
            int max = random.nextInt(51) + 50;
            int inventory = random.nextInt(31) + 20;
            Product product = new Product(productID += 1, products[proIn], price, inventory, min, max);
            if (i % 2 == 0 && i < numOfParts) {
                inv.addPart(new InHouse(partID += 1, parts[pIn], price / 25, inventory, min, max, inventory + max));
            } else if (i % 2 != 0 && i < numOfParts) {
                inv.addPart(new OutSourced(partID += 1, parts[pIn], price / 25, inventory, min, max, companys[nIn]));
            }
            product.addAssociatedPart(inv.getAllParts().getLast());
            inv.addProduct(product);
        }
    }


    // when this controller is being loaded, this setter loads a new part or product into the inventory
    public void loadControllerAddNewItem(Object item) {
        if (item instanceof Part part) {
            inv.addPart(part);
            partTable.setItems(inv.getAllParts());
        } else if (item instanceof Product product) {
            inv.addProduct(product);
            productTable.setItems(inv.getAllProducts());
        }
    }


    // when this controller is being loaded, this setter updates an existing part or product inside the inventory
    public void loadControllerUpdateExistingItem(Object item) {
        if (item instanceof Part part) {
            Part existing = inv.searchPartById(part.getId());
            int index = inv.getAllParts().indexOf(existing);
            inv.updatePart(index, part);
            partTable.setItems(inv.getAllParts());
        } else if (item instanceof Product product) {
            Product existing = inv.searchProductById(product.getId());
            int index = inv.getAllProducts().indexOf(existing);
            inv.updateProduct(index, product);
            productTable.setItems(inv.getAllProducts());
        }
    }



    // TableView Formatters --------------------------------------------------------------------------------------------
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



    // TextField Listeners ---------------------------------------------------------------------------------------------
    private void searchProductsTableListener(TextField field) { // reloads products table with filtered items
        field.textProperty().addListener((_, _, newStr) -> {
            ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
            Pattern errorPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).+$");
            Pattern letterPattern = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");
            Pattern numberPattern = Pattern.compile("^\\d+$");
            Matcher errorMatcher = errorPattern.matcher(newStr);
            Matcher letterMatcher = letterPattern.matcher(newStr);
            Matcher numberMatcher = numberPattern.matcher(newStr);
            if (newStr.isEmpty()) { // empty searchBar -> loads default table
                productTable.setItems(inv.getAllProducts());
            } else if (errorMatcher.matches()) {
                showErrorAlert("Cannot enter letters and numbers together.");
                productTable.setItems(inv.getAllProducts());
            } else if (letterMatcher.matches()) {
                filteredProducts = inv.searchProductByName(newStr);
                productTable.setItems(filteredProducts);
            } else if (numberMatcher.matches()) {
                filteredProducts.add(inv.searchProductById(Integer.parseInt(newStr)));
                productTable.setItems(filteredProducts);
            }
        });
    }


    private void searchPartsTableListener(TextField field) { // reloads parts table with filtered items when triggered
        field.textProperty().addListener((_, _, newStr) -> { // duplicate logic, but it makes code less complex
            ObservableList<Part> filteredParts = FXCollections.observableArrayList();
            Pattern errorPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).+$");
            Pattern letterPattern = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");
            Pattern numberPattern = Pattern.compile("^\\d+$");
            Matcher errorMatcher = errorPattern.matcher(newStr);
            Matcher letterMatcher = letterPattern.matcher(newStr);
            Matcher numberMatcher = numberPattern.matcher(newStr);
            if (newStr.isEmpty()) { // empty searchBar -> loads default table
                partTable.setItems(inv.getAllParts());
            } else if (errorMatcher.matches()) {
                showErrorAlert("Cannot enter letters and numbers together.");
                partTable.setItems(inv.getAllParts());
            } else if (letterMatcher.matches()) {
                filteredParts = inv.searchPartByName(newStr);
                partTable.setItems(filteredParts);
            } else if (numberMatcher.matches()) {
                filteredParts.add(inv.searchPartById(Integer.parseInt(newStr)));
                partTable.setItems(filteredParts);
            }
        });
    }
}
