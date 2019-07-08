package com.bry.firedonor.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.RequestItem;
import com.bry.firedonor.R;
import com.bry.firedonor.Services.DatabaseManager;

public class RequestsViewPagerFragment extends Fragment {
    private Context mContext;
    private RequestItem requestItem;
    private DonationItem donationItem;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setStuff(Context con, RequestItem item, DonationItem donItem){
        this.mContext = con;
        this.requestItem = item;
        this.donationItem = donationItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_requests_view_pager_item, container, false);

        TextView namePart = view.findViewById(R.id.namePart);
        TextView location = view.findViewById(R.id.location);
        TextView officialDate = view.findViewById(R.id.officialDate);
        TextView pickupVehicle = view.findViewById(R.id.pickupVehicle);
        Button acceptButton = view.findViewById(R.id.acceptButton);
        final TextView acceptedButton = view.findViewById(R.id.acceptedButton);

        namePart.setText(requestItem.getOrganisationName());
        location.setText(requestItem.getOrganisationLocation().getAreaDescription());
        officialDate.setText(requestItem.getPickUpDate().getOfficialDate());
        pickupVehicle.setText(String.format("%s, %s", requestItem.getVehicleDescription(), requestItem.getVehicleNumberPlate()));

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptedButton.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Accepted",Toast.LENGTH_SHORT).show();
                new DatabaseManager(mContext,"").setAcceptedRequest(requestItem,donationItem);
            }
        });

        if(donationItem.getAcceptedRequestItem()!=null){
            if(donationItem.getAcceptedRequestItem().equals(requestItem)){
                acceptedButton.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }
}
