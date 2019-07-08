package com.bry.firedonor.Services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.bry.firedonor.Constants;
import com.bry.firedonor.Models.DonationImage;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.RequestItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

public class DatabaseManager {
    private final String TAG = DatabaseManager.class.getSimpleName();
    private Context mContext;
    private String BROADCAST_RECEIVER = "";

    public DatabaseManager(Context context, String receiver){
        this.mContext = context;
        this.BROADCAST_RECEIVER = receiver;
    }

    public DatabaseManager setUpNewUserInFirebase(String username, String email){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid);
        usernameRef.child(Constants.USERNAME).setValue(username);
        usernameRef.child(Constants.EMAIL).setValue(email);
        usernameRef.child(Constants.TIME_OF_SIGNUP).setValue(Long.toString(Calendar.getInstance().getTimeInMillis()));

        return this;
    }

    private int iterator = 0;
    public DatabaseManager uploadDonationItem(DonationItem item){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<DonationImage> images = item.getItemImages();
        item.setItemImages(null);
        item.setUploaderId(uid);
        item.setUploaderName(new SharedPreferenceManager(mContext).loadNameInSharedPref());

        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS);
        donationRef.push();
        String pushRef = donationRef.getKey();
        item.setItemId(pushRef);
        donationRef.setValue(item);

        DatabaseReference donationInUserAccount = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid)
                .child(Constants.UPLOADED_DONATION_ITEMS).child(pushRef);
        donationInUserAccount.setValue(item);

        iterator = 0;
        final int imageNumber = images.size();
        for(DonationImage image:images){
            DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEM_IMAGES);
            imageRef.child(pushRef).child(image.getImageId()).setValue(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    iterator++;
                    if(iterator==imageNumber) LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(BROADCAST_RECEIVER));
                }
            });
        }

        return this;
    }

    public DatabaseManager uploadDonationRequest(RequestItem item){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid)
                .child(Constants.UPLOADED_DONATION_REQUESTS);
        requestRef.push();
        String pushRef = requestRef.getKey();

        item.setRequestId(pushRef);
        requestRef.setValue(item);

        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS).child(item.getDonationId())
                .child(Constants.UPLOADED_DONATION_REQUESTS).child(pushRef);
        donationRef.setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(BROADCAST_RECEIVER));
            }
        });

        return this;
    }

    public DatabaseManager updateNameDataInFirebase(DonationItem item ,String newName){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS).child(item.getItemId());
        donationRef.child("itemName").setValue(newName);

        DatabaseReference donationInUserAccount = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid)
                .child(Constants.UPLOADED_DONATION_ITEMS).child(item.getItemId());
        donationInUserAccount.child("itemName").setValue(newName);

        return this;
    }

    public DatabaseManager updateDetailsDataInFirebase(DonationItem item ,String newDetails){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS).child(item.getItemId());
        donationRef.child("itemDetails").setValue(newDetails);

        DatabaseReference donationInUserAccount = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid)
                .child(Constants.UPLOADED_DONATION_ITEMS).child(item.getItemId());
        donationInUserAccount.child("itemDetails").setValue(newDetails);

        return this;
    }

    public DatabaseManager updateImageDataInFirebase(DonationItem item, DonationImage dm){
        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEM_IMAGES);
        imageRef.child(item.getItemId()).child(dm.getImageId()).setValue(dm);

        return this;
    }

    public DatabaseManager deleteDonationItem(DonationItem item){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS);
        DatabaseReference donationInUserAccount = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid)
                .child(Constants.UPLOADED_DONATION_ITEMS).child(item.getItemId());

        donationRef.removeValue();
        donationInUserAccount.removeValue();

        return this;
    }

}
