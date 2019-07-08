package com.bry.firedonor.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bry.firedonor.Constants;
import com.bry.firedonor.Fragments.RequestsViewPagerFragment;
import com.bry.firedonor.Fragments.SentRequestsViewPagerFramgent;
import com.bry.firedonor.Models.DonationImage;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.RequestItem;
import com.bry.firedonor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyListedRequestsActivity extends AppCompatActivity {
    private final String TAG = MyDonationActivity.class.getSimpleName();
    private Context mContext;
    @Bind(R.id.backButton) ImageView backButton;
    @Bind(R.id.progressbarRelativeLayout) RelativeLayout progressbarRelativeLayout;
    @Bind(R.id.everythingRelativeLayout) LinearLayout everythingRelativeLayout;
    private boolean isShowingLoadingScreens = false;
    private List<RequestItem> loadedRequestedDonations = new ArrayList<>();
    @Bind(R.id.myRequestsViewPager) ViewPager myRequestsViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listed_requests);
        ButterKnife.bind(this);
        mContext = this.getApplicationContext();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadMyRequests(){
        showLoadingScreens();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid)
                .child(Constants.UPLOADED_DONATION_REQUESTS);
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                        RequestItem item = snap.getValue(RequestItem.class);
                        loadedRequestedDonations.add(item);
                    }
                    loadRequestedDonations();
                }else{
                    hideLoadingScreens();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int iteration = 0;
    private void loadRequestedDonations() {
        final int cycleNum = loadedRequestedDonations.size();
        for(final RequestItem item: loadedRequestedDonations){
            DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS).child(item.getDonationId());
            donationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        DonationItem donationItem = dataSnapshot.getValue(DonationItem.class);
                        loadedRequestedDonations.get(loadedRequestedDonations.indexOf(item)).setDonationItem(donationItem);
                    }
                    iteration++;
                    if(iteration==cycleNum){
                        loadOneImageForEach();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private int loadCount = 0;
    private void loadOneImageForEach() {
        loadCount = 0;
        final int count = loadedRequestedDonations.size();
        for(final RequestItem item: loadedRequestedDonations){
            if(item.getDonationItem().getItemImages().size()==0){
                //This means no images are in the item, therefore should be queried.
                DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEM_IMAGES)
                        .child(item.getDonationItem().getItemId());
                imageRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot snap:dataSnapshot.getChildren()) {
                                DonationImage image = snap.getValue(DonationImage.class);
                                loadedRequestedDonations.get(loadedRequestedDonations.indexOf(item)).getDonationItem().addItemImage(image);
                            }
                        }
                        loadCount++;
                        if(loadCount==count){
                            processLoadedImages();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void processLoadedImages() {
        LongOperationUnBakeThoseGodDamnImages operationUnBakeThoseGodDamnImages = new LongOperationUnBakeThoseGodDamnImages();
        operationUnBakeThoseGodDamnImages.execute("");
    }

    private class LongOperationUnBakeThoseGodDamnImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            for(RequestItem item:loadedRequestedDonations){
                if(item.getDonationItem().getItemImages().get(0).getImageBitmap()==null) {
                    for (DonationImage image : item.getDonationItem().getItemImages()) {
                        image.setImageBitmap(decodeFromFirebaseBase64(image.getEncodedImageString()));
                    }
                }
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result) {
            loadRequestItemsToViewPager();
            super.onPostExecute(result);
        }

    }

    private Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private void showLoadingScreens(){
        isShowingLoadingScreens = true;
        everythingRelativeLayout.setVisibility(View.GONE);
        progressbarRelativeLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreens(){
        isShowingLoadingScreens = false;
        everythingRelativeLayout.setVisibility(View.VISIBLE);
        progressbarRelativeLayout.setVisibility(View.GONE);
    }

    private void loadRequestItemsToViewPager(){
        MyPagerAdapter receivedRequestsFragmentPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        receivedRequestsFragmentPagerAdapter.setItemStuff(mContext,loadedRequestedDonations);
        myRequestsViewPager.setAdapter(receivedRequestsFragmentPagerAdapter);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private Context mCon;
        private List<RequestItem> sentRequests = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void setItemStuff(Context context, List<RequestItem> receivedReq){
            this.mCon = context;
            this.sentRequests = receivedReq;
        }

        @Override
        public int getCount() {
            return sentRequests.size();
        }

        @Override
        public Fragment getItem(int position) {
            SentRequestsViewPagerFramgent rv = new SentRequestsViewPagerFramgent();
            rv.setStuff(mCon,sentRequests.get(position));
            return rv;
        }

    }
}
