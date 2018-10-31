package models;

import java.util.ArrayList;

public class Seller extends Person {
    private int positiveSalesCount;
    private int negativeSalesCount;
    private ArrayList<Product> inventary;

    public Seller() {
        super();
    }

    public Seller(int id) {
        super(id);
    }

    public int getPositiveSalesCount() {
        return positiveSalesCount;
    }

    public void setPositiveSalesCount(int positiveSalesCount) {
        this.positiveSalesCount = positiveSalesCount;
    }

    public int getNegativeSalesCount() {
        return negativeSalesCount;
    }

    public void setNegativeSalesCount(int negativeSalesCount) {
        this.negativeSalesCount = negativeSalesCount;
    }

    public ArrayList<Product> getInventary() {
        return inventary;
    }

    public void setInventary(ArrayList<Product> inventary) {
        this.inventary = inventary;
    }
}
