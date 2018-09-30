package models;

import java.util.Calendar;
import java.util.List;

import enums.ProductSituation;
import enums.Status;

public class Product {
	private int id;
	private Seller seller;
	private ProductItem productItem;
	private Category category;
	private String title;
	private String description;
	private double price;
	private int soldQuantity;
	private int availableQuantity;
	private File thumbnail;
	private List<File> pictures;
	private ProductSituation situation;
	private Status status;
	private Calendar createdAt;
	private Calendar updatedAt;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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
	
	public File getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(File thumbnail) {
		this.thumbnail = thumbnail;
	}

	public List<File> getPictures() {
		return pictures;
	}

	public void setPictures(List<File> pictures) {
		this.pictures = pictures;
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

	public void toggleStatus() {
		if (this.status == Status.ACTIVE) {
			this.status = Status.INACTIVE;
		} else {
			this.status = Status.ACTIVE;
		}
	}

	public void setDefaultThumbnail(String baseUrlPath) {
		if (this.pictures != null && !this.pictures.isEmpty()) {
			this.thumbnail = this.pictures.get(this.pictures.size() - 1);
			return;
		}

		File defaultThumbnail = new File();

		defaultThumbnail.setName("picture-not-available");
		defaultThumbnail.setUrlPath(baseUrlPath + "/assets/images/thumbnail-not-available.jpg");
		this.thumbnail = defaultThumbnail;
	}
}
