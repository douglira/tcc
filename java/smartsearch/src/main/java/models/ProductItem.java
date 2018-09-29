package models;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class ProductItem {

	private int id;
	private ArrayList<Product> basedProducts;
	private String title;
	private ProductPicture thumbnail;
	private ArrayList<ProductPicture> pictures;
	private double marketPrice;
	private double maxPrice;
	private double minPrice;
	private int viewsCount;
	private int relevance;
	
	@Expose(deserialize = false, serialize = false)
	public static final int MAX_PICTURES = 15;

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
	
	public void addBasedProduct(Product product) {
		this.basedProducts.add(product);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ProductPicture getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(ProductPicture thumbnail) {
		this.thumbnail= thumbnail;
	}

	public ArrayList<ProductPicture> getPictures() {
		return pictures;
	}

	public void setPictures(ArrayList<ProductPicture> pictures) {
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
		
		double maxPrice = 0;
		double minPrice = Integer.MAX_VALUE;
		double sum = 0;
		
		for (Product product: this.basedProducts) {
			
			if (product.getPrice() > maxPrice) {
				maxPrice = product.getPrice();
			}
			
			if (product.getPrice() < minPrice ) {
				minPrice = product.getPrice();
			}
			
			sum += product.getPrice();
		}
		
		this.marketPrice = sum / this.basedProducts.size();
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
	}
}
