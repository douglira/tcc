package models;

import java.util.Calendar;

import enums.PeriodTime;
import enums.ShippingStatus;

public class Shipping {
	private int id;
	private double cost;
	private int deliveryTime;
	private PeriodTime deliveryPeriod;
	private ShippingStatus status;
	private Address receiverAddress;
	private Calendar updatedAt;
}
