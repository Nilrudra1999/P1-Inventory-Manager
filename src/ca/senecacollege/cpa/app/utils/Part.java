/****************************************************************************************************
 * P1-Inventory-Manager - Parts Abstract Class
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.utils;

/****************************************************************************************************
 * Abstract Base Class: Part - representing a mechanical part for commercial sale
 * This class cannot be instantiated alone but also implements its own getter and setter methods.
 * It acts as a sub-object within it's inherited class objects, encapsulating the following data:
 * A Part id, name, price, stock, min, and max values (centralizing duplicate code).
 ****************************************************************************************************/
public abstract class Part {
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    public Part(int id, String name, double price, int stock, int min, int max) {
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
}
