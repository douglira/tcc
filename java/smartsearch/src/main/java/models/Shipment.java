package models;

import enums.ShipmentMethod;
import enums.ShipmentStatus;

import java.util.Calendar;

public class Shipment {
    private Integer id;
    private Quote quote;
    private double cost;
    private Calendar estimatedTime;
    private ShipmentStatus status;
    private ShipmentMethod method;
    private Address receiverAddress;
    private Calendar createdAt;
    private Calendar updatedAt;

    public Shipment() {

    }

    public Shipment(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Calendar getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Calendar estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public ShipmentMethod getMethod() {
        return method;
    }

    public void setMethod(ShipmentMethod method) {
        this.method = method;
    }

    public Address getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(Address receiverAddress) {
        this.receiverAddress = receiverAddress;
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
}
