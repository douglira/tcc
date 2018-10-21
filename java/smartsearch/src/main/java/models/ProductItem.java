package models;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import enums.Status;

public class ProductItem extends ProductRepresentation {

    private ArrayList<Product> basedProducts;
    private double maxPrice;
    private double minPrice;
    private int viewsCount;
    private int relevance;

    private Status status;

    @Expose(deserialize = false, serialize = false)
    public static final int MAX_PICTURES = 15;

    public ProductItem() {
        super();
    }

    public ProductItem(Integer id) {
        super(id);
    }

    public ArrayList<Product> getBasedProducts() {
        return basedProducts;
    }

    public void setBasedProducts(ArrayList<Product> basedProducts) {
        this.basedProducts = basedProducts;
    }

    public void addBasedProduct(Product product) {
        this.basedProducts.add(product);
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addPicture(File picture) {
        this.pictures.add(picture);
    }

    public void updatePrices() {

        double maxPrice = 0;
        double minPrice = Integer.MAX_VALUE;
        double sum = 0;

        for (Product product : this.basedProducts) {

            if (product.getBasePrice() > maxPrice) {
                maxPrice = product.getBasePrice();
            }

            if (product.getBasePrice() < minPrice) {
                minPrice = product.getBasePrice();
            }

            sum += product.getBasePrice();
        }

        this.basePrice = sum / this.basedProducts.size();
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }
}
