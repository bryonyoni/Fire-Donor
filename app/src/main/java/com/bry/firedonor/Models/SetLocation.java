package com.bry.firedonor.Models;

import com.google.android.gms.maps.model.LatLng;

public class SetLocation {
    private double latitude;
    private double longitude;
    private String areaDescription = "";

    public SetLocation(){}

    public SetLocation(double lat,double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public SetLocation(LatLng latLng){
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }
}
