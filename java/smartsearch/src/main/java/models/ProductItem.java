package models;

import java.util.ArrayList;

public class ProductItem {

	private int id;
	private ArrayList<Product> basedProducts;
	private String title;
	private String thumbnailPath;
	private ArrayList<String> pictures;
	private double marketPrice;
	private double maxPrice;
	private double minPrice;
	private int viewsCount;
	private int relevance;

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	public ArrayList<Product> getBasedProducts() {
		return basedProducts;
	}

	public void setBasedProducts(ArrayList<Product> basedProducts) {
		this.basedProducts = basedProducts;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public ArrayList<String> getPictures() {
		return pictures;
	}

	public void setPictures(ArrayList<String> pictures) {
		this.pictures = pictures;
	}

	public double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
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

	public void updatePrices() {		
		this.marketPrice = this.basedProducts.stream()
				.mapToDouble(Product::getPrice)
				.average()
				.getAsDouble();
		this.maxPrice = this.basedProducts.stream()
				.mapToDouble(Product::getPrice)
				.max()
				.getAsDouble();
		this.minPrice = this.basedProducts.stream()
				.mapToDouble(Product::getPrice)
				.min()
				.getAsDouble();
	}
}
