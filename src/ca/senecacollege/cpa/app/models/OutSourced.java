/****************************************************************************************************
 * P1-Inventory-Manager - Out Sourced Inherited Parts Class
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.models;

import ca.senecacollege.cpa.app.utils.Part;

/****************************************************************************************************
 * Concrete Model Class: OutSourced Part - representing a mechanical part, production's outsourced
 * This class can be instantiated through an overloaded constructor and inherits from its parent
 * class (Part). It represents a machine Part that's outsourced specifically, and contains an
 * additional data member (companyName) as well as a getter and setter methods for it.
 ****************************************************************************************************/
public class OutSourced extends Part {
    String companyName;

    // overloaded constructor creates a sub-object of the base class and sets the additional attribute's value
    public OutSourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyName() { return companyName; }
}
