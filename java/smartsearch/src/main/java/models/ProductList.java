package models;

import java.util.Objects;

public class ProductList implements Comparable{
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductList that = (ProductList) o;
		return Objects.equals(product.getId(), that.product.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(product);
	}

	@Override
	public int compareTo(Object o) {
		ProductList pl = (ProductList) o;
		return pl.getProduct().getId().compareTo(pl.getProduct().getId());
	}
}
