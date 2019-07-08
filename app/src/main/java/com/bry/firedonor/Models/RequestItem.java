package com.bry.firedonor.Models;

public class RequestItem {
    private String requestId;
    private String donationId;
    private String requesterUid;
    private String organisationName;
    private SetLocation organisationLocation;
    private MyTime pickUpDate;
    private String vehicleDescription;
    private String vehicleNumberPlate;
    private DonationItem donationItem;

    public RequestItem(){}

    public RequestItem(String organisationName,SetLocation location, MyTime setTime, String vehicleDesc, String vehicleNumberPlate){
        this.organisationName = organisationName;
        this.organisationLocation = location;
        this.pickUpDate = setTime;
        this.vehicleDescription = vehicleDesc;
        this.vehicleNumberPlate = vehicleNumberPlate;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public SetLocation getOrganisationLocation() {
        return organisationLocation;
    }

    public void setOrganisationLocation(SetLocation organisationLocation) {
        this.organisationLocation = organisationLocation;
    }

    public MyTime getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(MyTime pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }

    public String getVehicleNumberPlate() {
        return vehicleNumberPlate;
    }

    public void setVehicleNumberPlate(String vehicleNumberPlate) {
        this.vehicleNumberPlate = vehicleNumberPlate;
    }

    public String getRequesterUid() {
        return requesterUid;
    }

    public void setRequesterUid(String requesterUid) {
        this.requesterUid = requesterUid;
    }

    public DonationItem getDonationItem() {
        return donationItem;
    }

    public void setDonationItem(DonationItem donationItem) {
        this.donationItem = donationItem;
    }
}
