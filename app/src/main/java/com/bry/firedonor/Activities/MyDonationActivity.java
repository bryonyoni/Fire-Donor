package com.bry.firedonor.Activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bry.firedonor.Fragments.RequestsViewPagerFragment;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.RequestItem;
import com.bry.firedonor.R;
import com.bry.firedonor.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyDonationActivity extends AppCompatActivity {
    private final String TAG = MyDonationActivity.class.getSimpleName();
    private Context mContext;
    private DonationItem selectedDonatedItem;
    @Bind(R.id.mainImage) ImageView mainImage;
    @Bind(R.id.itemNameAndMass) TextView itemNameAndMass;
    @Bind(R.id.itemListingDate) TextView itemListingDate;
    @Bind(R.id.receivedRequestsViewPager) ViewPager receivedRequestsViewPager;
    @Bind(R.id.backButton) ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donation);
        ButterKnife.bind(this);
        mContext = this.getApplicationContext();

        selectedDonatedItem = Variables.selectedDonationItem;

        mainImage.setImageBitmap(selectedDonatedItem.getItemImages().get(0).getImageBitmap());
        itemNameAndMass.setText(selectedDonatedItem.getItemName());
        itemListingDate.setText(selectedDonatedItem.getTimeOfCreation().getOfficialDate());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadRequests();
    }

    private void loadRequests(){
        MyPagerAdapter receivedRequestsFragmentPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        receivedRequestsFragmentPagerAdapter.setItemStuff(mContext,selectedDonatedItem.getDonationRequests(),selectedDonatedItem);
        receivedRequestsViewPager.setAdapter(receivedRequestsFragmentPagerAdapter);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private Context mCon;
        private List<RequestItem> receivedRequests = new ArrayList<>();
        private DonationItem donationItem;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void setItemStuff(Context context, List<RequestItem> receivedReq, DonationItem item){
            this.mCon = context;
            this.receivedRequests = receivedReq;
            this.donationItem = item;
        }

        @Override
        public int getCount() {
            return receivedRequests.size();
        }

        @Override
        public Fragment getItem(int position) {
            RequestsViewPagerFragment rv = new RequestsViewPagerFragment();
            rv.setStuff(mCon,receivedRequests.get(position),donationItem);
            return rv;
        }

    }
}
