package com.bry.firedonor.Models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DonationItem {
    private String itemName;
    private String itemId;
    private String uploaderId;
    private List<DonationImage> itemImages = new ArrayList<>();
    private String mainImageId;
    private MyTime timeOfCreation;
    private String itemDetails;
    private SetLocation itemLocation;

    public DonationItem(){}

    public DonationItem(String itemName, List<DonationImage> itemImages, String mainImageId, String itemDetails, SetLocation itemLocation){
        this.itemName = itemName;
        this.itemImages = itemImages;
        this.mainImageId = mainImageId;
        this.itemDetails = itemDetails;
        this.itemLocation = itemLocation;
        timeOfCreation = new MyTime(Calendar.getInstance());
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public List<DonationImage> getItemImages() {
        return itemImages;
    }

    public void setItemImages(List<DonationImage> itemImages) {
        this.itemImages = itemImages;
    }

    public String getMainImageId() {
        return mainImageId;
    }

    public void setMainImageId(String mainImageId) {
        this.mainImageId = mainImageId;
    }

    public MyTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(MyTime timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public String getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }

    public SetLocation getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(SetLocation itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }
}
