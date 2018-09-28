package models;

import com.google.gson.annotations.Expose;

public class ProductPicture {
	private Integer id;
	private String filename;
	private String name;
	private String urlPath;
	private double size;
	private String type;
	private ProductItem productItem;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrlPath() {
		return urlPath;
	}
	
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ProductItem getProductItem() {
		return productItem;
	}
	
	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}
}
