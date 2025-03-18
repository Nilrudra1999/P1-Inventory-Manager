/****************************************************************************************************
 * P1-Inventory-Manager - Inventory Class
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.models;

import ca.senecacollege.cpa.app.utils.Part;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/****************************************************************************************************
 * Model Class: Inventory - representing the manufacture's inventory of products
 * This class contains a list of parts and products, the total amount of stuff within the inventory.
 * It also contains methods to add and delete parts or products, update parts or products, and
 * search for parts or products by name or by id.
 ****************************************************************************************************/
public class Inventory {
    private ObservableList<Part> allParts = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    public void addPart(Part newPart) {
        allParts.add(newPart);
    }

    public void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    public ObservableList<Part> getAllParts() { return allParts; }
    public ObservableList<Product> getAllProducts() { return allProducts; }

    // search returns a single part which matches the first occurrence of id value
    public Part searchPartById(int partId) {
        for (Part part : allParts) {
            if (part.getId() == partId) {
                return part;
            }
        }
        return null;
    }

    // search returns a single product which matches the first occurrence of the id value
    public Product searchProductById(int productId) {
        for (Product product: allProducts) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }

    // search returns a list of parts that have names which contains the parameter string
    public ObservableList<Part> searchPartByName(String name) {
        ObservableList<Part> rcPartsList = FXCollections.observableArrayList();
        for (Part part : allParts) {
            if (part.getName().toLowerCase().contains(name.toLowerCase())) {
                rcPartsList.add(part);
            }
        }
        return rcPartsList;
    }

    // search returns a list of products that have names which contains the parameter string
    public ObservableList<Product> searchProductByName(String name) {
        ObservableList<Product> rcProductsList = FXCollections.observableArrayList();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                rcProductsList.add(product);
            }
        }
        return rcProductsList;
    }

    public void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    public void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
    }

    public boolean deletePart(Part selectedPart) {
        return allParts.remove(selectedPart);
    }

    public boolean deleteProduct(Product selectedProduct) {
        return allProducts.remove(selectedProduct);
    }
}
