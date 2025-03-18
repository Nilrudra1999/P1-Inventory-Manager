/****************************************************************************************************
 * P1-Inventory-Manager - In-House Inherited Parts Class
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.models;

import ca.senecacollege.cpa.app.utils.Part;

/****************************************************************************************************
 * Concrete Model Class: InHouse Part - representing a mechanical part produced in-house
 * This class can be instantiated through an overloaded constructor and inherits from its parent
 * class (Part). It represents a machine Part that's built in-house specifically, and contains an
 * additional data member (machineId) as well as a getter and setter methods for it.
 ****************************************************************************************************/
public class InHouse extends Part {
    private int machineId;

    // overloaded constructor calls the base class constructor, creating a sub-object inside this object
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    public void setMachine(int machineId) { this.machineId = machineId; }
    public int getMachine() { return machineId; }
}
