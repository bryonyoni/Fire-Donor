package com.bry.firedonor.Models;


import android.graphics.Bitmap;

public class DonationImage {
    private Bitmap mImageBitmap;
    private String imageId;
    private String encodedImageString;

    public DonationImage(){}

    public DonationImage(String imageId, Bitmap imageBitmap){
        this.imageId = imageId;
        this.mImageBitmap = imageBitmap;
    }


    public Bitmap getImageBitmap() {
        return mImageBitmap;
    }

    public void setImageBitmap(Bitmap mImageBitmap) {
        this.mImageBitmap = mImageBitmap;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getEncodedImageString() {
        return encodedImageString;
    }

    public void setEncodedImageString(String encodedImageString) {
        this.encodedImageString = encodedImageString;
    }
}
