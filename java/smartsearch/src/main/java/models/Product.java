package models;

import java.util.Calendar;

import enums.ProductSituation;
import enums.Status;

public class Product extends ProductRepresentation {
	private Seller seller;
	private ProductItem productItem;
	private Category category;
	private String description;
	private int soldQuantity;
	private int availableQuantity;
	private ProductSituation situation;
	private Status status;

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public ProductItem getProductItem() {
		return productItem;
	}

	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSoldQuantity() {
		return soldQuantity;
	}

	public void setSoldQuantity(int soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

	public int getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
	
	public ProductSituation getSituation() {
		return situation;
	}
	
	public void setSituation(ProductSituation situation) {
		this.situation = situation;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void toggleStatus() {
		if (this.status == Status.ACTIVE) {
			this.status = Status.INACTIVE;
		} else {
			this.status = Status.ACTIVE;
		}
	}
}
