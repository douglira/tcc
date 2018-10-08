package models.socket;

import models.PurchaseRequest;
import models.User;

public class PRCreation {
    private User to = new User();
    private PurchaseRequest purchaseRequest;

    public PRCreation() {

    }

    public PRCreation(User to, PurchaseRequest purchaseRequest) {
        this.to = to;
        this.purchaseRequest = purchaseRequest;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public PurchaseRequest getPurchaseRequest() {
        return purchaseRequest;
    }

    public void setPurchaseRequest(PurchaseRequest purchaseRequest) {
        this.purchaseRequest = purchaseRequest;
    }
}
