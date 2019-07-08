package com.bry.firedonor.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.RequestItem;
import com.bry.firedonor.R;
import com.bry.firedonor.Services.DatabaseManager;

public class SentRequestsViewPagerFramgent extends Fragment {
    private Context mContext;
    private RequestItem requestItem;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setStuff(Context con, RequestItem item){
        this.mContext = con;
        this.requestItem = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_submitted_request_item, container, false);

        ImageView mainImage = view.findViewById(R.id.mainImage);
        TextView itemNameAndMass = view.findViewById(R.id.itemNameAndMass);
        TextView timeListed = view.findViewById(R.id.timeListed);
        TextView organisation = view.findViewById(R.id.organisation);
        TextView pickUpDate = view.findViewById(R.id.pickUpDate);
        TextView vehicleDesc = view.findViewById(R.id.vehicleDesc);
        TextView organisationLocation = view.findViewById(R.id.organisationLocation);
        TextView status = view.findViewById(R.id.status);

        try {
            mainImage.setImageBitmap(requestItem.getDonationItem().getItemImages().get(0).getImageBitmap());
        }catch (Exception e){e.printStackTrace();}

        itemNameAndMass.setText(requestItem.getDonationItem().getItemName());
        timeListed.setText(requestItem.getDonationItem().getTimeOfCreation().getCasualDate());
        organisation.setText(requestItem.getOrganisationName());
        pickUpDate.setText(requestItem.getPickUpDate().getOfficialDate());
        vehicleDesc.setText(String.format("%s, %s", requestItem.getVehicleDescription(), requestItem.getVehicleNumberPlate()));
        organisationLocation.setText(requestItem.getOrganisationLocation().getAreaDescription());

        if(requestItem.getDonationItem().getAcceptedRequestItem()!=null){
            if(requestItem.getDonationItem().getAcceptedRequestItem().getRequestId().equals(requestItem.getRequestId())) status.setText("Accepted.");
            else status.setText("Not Accepted.");
        }else{
            status.setText("Pending.");
        }

        return view;
    }
}
