package models;

import enums.QuoteStatus;

import java.util.ArrayList;
import java.util.Calendar;

public class Quote {
    private int id;
    private Seller seller;
    private PurchaseRequest purchaseRequest;
    private ArrayList<Item> customListProduct;
    private ArrayList<Shipment> shipmentOptions;
    private String additionalData;
    private QuoteStatus status;
    private double discount;
    private double totalAmount;
    private Calendar expirationDate;
    private Calendar createdAt;
    private Calendar updatedAt;

    public Quote() {

    }

    public Quote(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public PurchaseRequest getPurchaseRequest() {
        return purchaseRequest;
    }

    public void setPurchaseRequest(PurchaseRequest purchaseRequest) {
        this.purchaseRequest = purchaseRequest;
    }

    public ArrayList<Item> getCustomListProduct() {
        return customListProduct;
    }

    public void setCustomListProduct(ArrayList<Item> customListProduct) {
        this.customListProduct = customListProduct;
    }

    public ArrayList<Shipment> getShipmentOptions() {
        return shipmentOptions;
    }

    public void setShipmentOptions(ArrayList<Shipment> shipmentOptions) {
        this.shipmentOptions = shipmentOptions;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public Calendar getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Calendar updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Item validateProductsAvailability() {
        return this.customListProduct.stream()
                .filter(quoteItem -> quoteItem.getQuantity() > ((Product) quoteItem.getProduct()).getAvailableQuantity())
                .findFirst()
                .orElse(null);
    }

    public boolean isExpired() {
        if (Calendar.getInstance().after(this.expirationDate)) {
            this.status = QuoteStatus.EXPIRED;
            return true;
        }
        return false;
    }

    public void calculateTotalAmount() {
        this.totalAmount = this.customListProduct.stream()
                .mapToDouble(Item::getSubtotalAmount)
                .sum();

        if (this.discount > 0) {
            this.totalAmount *= (1 - (this.discount / 100));
        }
    }
}
