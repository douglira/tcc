package models;

public class ProductList {
	private ProductRepresentation product;
	private String additionalSpec;
	private int quantity;
	private double subtotalAmount;

	public ProductList() {

	}

	public ProductList(ProductRepresentation product) {
		this.product = product;
	}

	public ProductRepresentation getProduct() {
		return product;
	}

	public void setProduct(ProductRepresentation product) {
		this.product = product;
	}

	public String getAdditionalSpec() {
		return additionalSpec;
	}

	public void setAdditionalSpec(String additionalSpec) {
		this.additionalSpec = additionalSpec;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getSubtotalAmount() {
		return subtotalAmount;
	}

	public void calculateAmount() {
        this.subtotalAmount = this.product.basePrice * this.quantity;
    }
}
