package models;

import enums.PRStage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseRequest {
    private int id;
    private Buyer buyer;
    private ArrayList<ProductList> listProducts = new ArrayList<ProductList>();
    private ArrayList<Quote> quotes;
    private int propagationCount;
    private String additionalData;
    private PRStage stage;
    private Calendar dueDateAverage;
    private int viewsCount;
    private double totalAmount;
    private Calendar createdAt;
    private Calendar updatedAt;
    private Calendar closedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public ArrayList<ProductList> getListProducts() {
        return listProducts;
    }

    public void setListProducts(ArrayList<ProductList> listProducts) {
        this.listProducts = listProducts;
    }

    public ArrayList<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(ArrayList<Quote> quotes) {
        this.quotes = quotes;
    }

    public int getPropagationCount() {
        return propagationCount;
    }

    public void setPropagationCount(int propagationCount) {
        this.propagationCount = propagationCount;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public PRStage getStage() {
        return stage;
    }

    public void setStage(PRStage status) {
        this.stage = status;
    }

    public Calendar getDueDateAverage() {
        return dueDateAverage;
    }

    public void setDueDateAverage(Calendar dueDateAverage) {
        this.dueDateAverage = dueDateAverage;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
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

    public Calendar getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Calendar closedAt) {
        this.closedAt = closedAt;
    }

    public void addListProduct(ProductList productList) {
        this.listProducts.add(productList);
    }

    public void calculateAmount() {
        this.totalAmount = this.listProducts.stream().mapToDouble(ProductList::getSubtotalAmount).sum();
    }

    public void calculateDueDateAverage(ArrayList<Seller> sellers) {
        if (sellers == null || sellers.isEmpty()) {
            this.dueDateAverage = Calendar.getInstance();
            return;
        }

        List<Integer> days = sellers
                .stream()
                .mapToInt(Seller::getQuotesExpirationPeriod)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        int index;
        if (days.size() == 1) {
            index = 0;
        } else {
            index = ((int) Math.ceil(days.size() / 2)) - 1;
        }
        int daysResult = days.get(index);

        Calendar dueDateAverage = Calendar.getInstance();
        dueDateAverage.add(Calendar.DAY_OF_YEAR, daysResult);
        this.dueDateAverage = dueDateAverage;
    }
}
