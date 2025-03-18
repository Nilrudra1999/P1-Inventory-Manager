/****************************************************************************************************
 * P1-Inventory-Manager - Product Class
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.models;

import ca.senecacollege.cpa.app.utils.Part;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/****************************************************************************************************
 * Model Class: Product - representing a complete mechanical product
 * This class can be instantiated through an overloaded constructor. It represents a product the
 * company produces and contains the following data members: a list of associated parts, id, name,
 * price, stock, the min, and max values along with getters and setters for all attributes.
 ****************************************************************************************************/
public class Product {
    private ObservableList<Part> associatedParts;
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    // overloaded constructor sets all data member values but only makes empty observableArrayList for parts
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.associatedParts = FXCollections.observableArrayList();
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setMin(int min) { this.min = min; }
    public void setMax(int max) { this.max = max; }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getMin() { return min; }
    public int getMax() { return max; }

    public void addAssociatedPart(Part part) {
        this.associatedParts.add(part);
    }

    public void deleteAssociatedPart(Part selectedAssociatedPart) {
        this.associatedParts.remove(selectedAssociatedPart);
    }

    // returns a reference to the observableArrayList of parts, useful for deleting and modifying
    public ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }
}
