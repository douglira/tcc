package models;

import enums.ShippingStatus;

import java.util.Calendar;

public class Shipping {
    private int id;
    private double cost;
    private Calendar estimatedTime;
    private Calendar handledTime;
    private ShippingStatus status;
    private Address receiverAddress;
    private Calendar createdAt;
    private Calendar updatedAt;
}
