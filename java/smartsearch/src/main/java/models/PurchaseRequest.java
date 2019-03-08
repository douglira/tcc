package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import enums.PRStage;

public class PurchaseRequest {
    private Integer id;
    private Buyer buyer;
    private ArrayList<Item> listProducts = new ArrayList<Item>();
    private ArrayList<Quote> quotes;
    private int propagationCount;
    private String additionalData;
    private PRStage stage;
    private Calendar dueDate;
    private boolean quotesVisibility;
    private int viewsCount;
    private double totalAmount;
    private Calendar createdAt;
    private Calendar updatedAt;
    private Calendar closedAt;

    public PurchaseRequest() {

    }

    public PurchaseRequest(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public ArrayList<Item> getListProducts() {
        return listProducts;
    }

    public void setListProducts(ArrayList<Item> listProducts) {
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

    public void setStage(PRStage stage) {
        this.stage = stage;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public boolean getQuotesVisibility() {
        return quotesVisibility;
    }

    public void setQuotesVisibility(boolean quotesVisibility) {
        this.quotesVisibility = quotesVisibility;
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

    public void addListProduct(Item item) {
        this.listProducts.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseRequest that = (PurchaseRequest) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void calculateAmount() {
        this.totalAmount = this.listProducts.stream().mapToDouble(Item::getSubtotalAmount).sum();
    }

    public boolean validateDueDate() {
        Date dueDate = this.getDueDate().getTime();
        Date now = new Date();

        long diff = dueDate.getTime() - now.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.setTime(dueDate);
        this.setDueDate(dueDateCalendar);

        return days > 90 || days <= 0;
    }
}
