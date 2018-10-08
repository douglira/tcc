package models;

import java.util.ArrayList;

public abstract class ProductRepresentation {
    protected int id;
    protected String title;
    protected double basePrice;
    protected File thumbnail;
    protected ArrayList<File> pictures;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public File getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(File thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ArrayList<File> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<File> pictures) {
        this.pictures = pictures;
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
