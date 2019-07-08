package com.bry.firedonor.Activities;

import android.Manifest;
import android.animation.Animator;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.firedonor.Adapters.ListedDonationItemAdapter;
import com.bry.firedonor.Constants;
import com.bry.firedonor.Fragments.FeedbackFragment;
import com.bry.firedonor.Models.DonationImage;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.R;
import com.bry.firedonor.Services.Utils;
import com.bry.firedonor.Variables;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener
        ,GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener{
    private final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    @Bind(R.id.everythingRelativeLayout) RelativeLayout everythingRelativeLayout;
    private boolean isShowingLoadingScreens = false;
    @Bind(R.id.progressbarRelativeLayout) RelativeLayout progressbarRelativeLayout;

    @Bind(R.id.openFeedbackButton) ImageButton openFeedbackButton;
    @Bind(R.id.userAccountButton) ImageButton userAccountButton;

    @Bind(R.id.myDonationsLinearLayout) LinearLayout myDonationsLinearLayout;
    @Bind(R.id.newDonationLinearLayout) LinearLayout newDonationLinearLayout;
    @Bind(R.id.shareAppLinearLayout) LinearLayout shareAppLinearLayout;
    @Bind(R.id.myRequestsLinearLayout) LinearLayout myRequestsLinearLayout;

    @Bind(R.id.listedItemsRecyclerView) RecyclerView listedItemsRecyclerView;
    List<DonationItem> loadedDonationItems = new ArrayList<>();
    private DonationItem donationItemToBeViewed;
    ListedDonationItemAdapter listedDonationItemAdapter;
    @Bind(R.id.loadingText) TextView loadingText;

    @Bind(R.id.bottomNavButtonsLinearLayout) LinearLayout bottomNavButtonsLinearLayout;
    private boolean isBottomNavButtonsLayoutVisible = true;
    @Bind(R.id.collapsedNavButtonsLinearLayout) LinearLayout collapsedNavButtonsLinearLayout;
    @Bind(R.id.collapsedImagesLinearLayout) LinearLayout collapsedImagesLinearLayout;

    @Bind(R.id.viewDonationRelativeLayout) RelativeLayout viewDonationRelativeLayout;
    private boolean isViewDonationLayoutVisible = false;
    @Bind(R.id.backButton) ImageView backButton;
    @Bind(R.id.uploaderTextView) TextView uploaderTextView;
    @Bind(R.id.flagButton) ImageView flagButton;
    @Bind(R.id.titleAndWeightTextView) TextView titleAndWeightTextView;
    @Bind(R.id.mainImageImageView) ImageView mainImageImageView;
    @Bind(R.id.smallImage1) ImageView smallImage1;
    @Bind(R.id.smallImage2) ImageView smallImage2;
    @Bind(R.id.smallImage3) ImageView smallImage3;
    @Bind(R.id.viewImagesRelativeLayoutButton) RelativeLayout viewImagesRelativeLayoutButton;
    @Bind(R.id.donationDetailsTextV) TextView donationDetailsTextV;
    @Bind(R.id.formalTimeTextView) TextView formalTimeTextView;
    @Bind(R.id.informalTimeTextView) TextView informalTimeTextView;
    @Bind(R.id.locationDescriptionTextView) TextView locationDescriptionTextView;
    @Bind(R.id.openMapImageView) ImageView openMapImageView;
    @Bind(R.id.requestCardView) CardView requestCardView;

    private GoogleMap map;
    private double CBD_LAT = -1.2805;
    private double CBD_LONG = 36.8163;
    private LatLng CBD = new LatLng(CBD_LAT, CBD_LONG);
    private int ZOOM = 15;
    private Marker mySetMarker;
    private final int REQUESTCODE = 3301;
    @Bind(R.id.mapRelativeLayout) RelativeLayout mapRelativeLayout;
    @Bind(R.id.mapContainer) CardView mapContainer;
    @Bind(R.id.setLocationButton) CardView setLocationButton;
    private boolean isMapOpen = false;

    private int mAnimationTime = 300;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this.getApplicationContext();

        loadDonationItems();

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverForViewDonation,new IntentFilter(Constants.VIEW_DONATION_INTENT_FILTER));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverForRequestButton,new IntentFilter(Constants.REQUEST_DONATION_INTENT_FILTER));

        openFeedbackButton.setOnClickListener(this);
        userAccountButton.setOnClickListener(this);
        myDonationsLinearLayout.setOnClickListener(this);
        newDonationLinearLayout.setOnClickListener(this);
        shareAppLinearLayout.setOnClickListener(this);
        myRequestsLinearLayout.setOnClickListener(this);
    }

    private void loadDonationItems() {
        showLoadingScreens();
        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS);
        donationRef.limitToFirst(Constants.LOADING_NUMBER_LIMIT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                        DonationItem item = snap.getValue(DonationItem.class);
                        if(snap.child(Constants.NUMBER_OF_FLAGS).exists()) {
                            Long numberOfReports = snap.child(Constants.NUMBER_OF_FLAGS).getChildrenCount();
                            item.setNumberOfReports(numberOfReports.intValue());
                        }
                        if(!item.hasBeenTakenDown() && !item.doesAcceptedRequestItemExist()){
                            loadedDonationItems.add(item);
                        }
                    }
                    loadItemImages();
                }else{
                    hideLoadingScreens();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int loadCount = 0;
    private void loadItemImages() {
        loadCount = 0;
        final int count = loadedDonationItems.size();
        for(final DonationItem item: loadedDonationItems){
            if(item.getItemImages().size()==0){
                //This means no images are in the item, therefore should be queried.
                DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEM_IMAGES).child(item.getItemId());
                imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot snap:dataSnapshot.getChildren()) {
                                DonationImage image = snap.getValue(DonationImage.class);
                                loadedDonationItems.get(loadedDonationItems.indexOf(item)).addItemImage(image);
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
        MainActivity.LongOperationUnBakeThoseGodDamnImages operationUnBakeThoseGodDamnImages = new LongOperationUnBakeThoseGodDamnImages();
        operationUnBakeThoseGodDamnImages.execute("");
    }

    private class LongOperationUnBakeThoseGodDamnImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            for(DonationItem item:loadedDonationItems){
                if(item.getItemImages().get(0).getImageBitmap()==null) {
                    for (DonationImage image : item.getItemImages()) {
                        image.setImageBitmap(decodeFromFirebaseBase64(image.getEncodedImageString()));
                    }
                }
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result) {
            loadDonationItemsToRecyclerView();
            super.onPostExecute(result);
        }

    }

    private Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private void loadDonationItemsToRecyclerView() {
        //finally load the prepared Donation items to the recyclerview
        listedDonationItemAdapter = new ListedDonationItemAdapter(loadedDonationItems, MainActivity.this, mContext);
        listedItemsRecyclerView.setAdapter(listedDonationItemAdapter);
        listedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        listedDonationItemAdapter.setOnBottomReachedListener(new ListedDonationItemAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                loadMoreDonatedItems();
                loadingText.setVisibility(View.VISIBLE);
            }
        });
    }

    BroadcastReceiver mMessageReceiverForRequestButton = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentt = new Intent(MainActivity.this,RequestDonationActivity.class);
            startActivity(intentt);
        }
    };

    BroadcastReceiver mMessageReceiverForViewDonation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            donationItemToBeViewed = Variables.selectedDonationItem;
            loadViewDonationPart();
        }
    };

    private void loadMoreDonatedItems(){
        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEMS);
        donationRef.startAt(loadedDonationItems.get(loadedDonationItems.size()-1).getItemId()).limitToFirst(Constants.LOADING_NUMBER_LIMIT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DonationItem> newItems = new ArrayList<>();
                if(dataSnapshot.hasChildren()){
                    int i = 0;
                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                        DonationItem item = snap.getValue(DonationItem.class);
                        if(snap.child(Constants.NUMBER_OF_FLAGS).exists()) {
                            Long numberOfReports = snap.child(Constants.NUMBER_OF_FLAGS).getChildrenCount();
                            item.setNumberOfReports(numberOfReports.intValue());
                        }
                        if(!item.hasBeenTakenDown() && !item.doesAcceptedRequestItemExist()){
                            if(i==0) {
                            }else{
                                newItems.add(item);
                            }
                            i++;
                        }
                    }
                }else{
//                    no more items exist.
                }
                loadMoreItemImages(newItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int loadCountMore = 0;
    private void loadMoreItemImages(final List<DonationItem> newItems) {
        loadCountMore = 0;
        final int count = newItems.size();
        for(final DonationItem item: newItems){
            if(item.getItemImages().size()==0){
                //This means no images are in the item, therefore should be queried.
                DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference(Constants.UPLOADED_DONATION_ITEM_IMAGES).child(item.getItemId());
                imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot snap:dataSnapshot.getChildren()) {
                                DonationImage image = snap.getValue(DonationImage.class);
                                newItems.get(newItems.indexOf(item)).addItemImage(image);
                            }
                        }
                        loadCountMore++;
                        if(loadCountMore==count){
                            processMoreLoadedImages(newItems);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private List<DonationItem> newItemsToUnBake = new ArrayList<>();
    private void processMoreLoadedImages(final List<DonationItem> newItems) {
        MainActivity.LongOperationUnBakeThoseGodDamnNewImages operationUnBakeThoseGodDamnImages = new LongOperationUnBakeThoseGodDamnNewImages();
        newItemsToUnBake = newItems;
        operationUnBakeThoseGodDamnImages.execute("");
    }

    private class LongOperationUnBakeThoseGodDamnNewImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            for(DonationItem item:newItemsToUnBake){
                if(item.getItemImages().get(0).getImageBitmap()==null) {
                    for (DonationImage image : item.getItemImages()) {
                        image.setImageBitmap(decodeFromFirebaseBase64(image.getEncodedImageString()));
                    }
                }
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result) {
            loadNewItemsToRecyclerView();
            super.onPostExecute(result);
        }

    }

    private void loadNewItemsToRecyclerView(){
        loadedDonationItems.addAll(newItemsToUnBake);
        for(DonationItem item: newItemsToUnBake){
            listedDonationItemAdapter.addDonationItemToList(item);
            listedDonationItemAdapter.notifyItemInserted(newItemsToUnBake.size() - 1);
            listedDonationItemAdapter.notifyDataSetChanged();
        }
        loadingText.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        if(v.equals(openFeedbackButton)){
            openFeedBackForm();
        } else if(v.equals(userAccountButton)){
            Intent intent = new Intent(MainActivity.this, UserAccountActivity.class);
            startActivity(intent);
        } else if(v.equals(myDonationsLinearLayout)){
            Intent intent = new Intent(MainActivity.this, MyDonationsActivity.class);
            startActivity(intent);
        } else if(v.equals(newDonationLinearLayout)){
            Intent intent = new Intent(MainActivity.this, NewDonationActivity.class);
            startActivity(intent);
        } else if(v.equals(shareAppLinearLayout)){
            Vibrator s = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            s.vibrate(50);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.shareText2));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.shareText)));
        } else if(v.equals(myRequestsLinearLayout)){
            Intent intent = new Intent(MainActivity.this, MyListedRequestsActivity.class);
            startActivity(intent);
        }
    }

    private void openFeedBackForm() {
        FragmentManager fm = getFragmentManager();
        FeedbackFragment reportDialogFragment = new FeedbackFragment();
        reportDialogFragment.setMenuVisibility(false);
        reportDialogFragment.show(fm, "Feedback.");
        reportDialogFragment.setfragContext(mContext);
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

    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForRequestButton);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForViewDonation);
        super.onDestroy();
    }

    private void loadViewDonationPart() {
        isViewDonationLayoutVisible = true;

        viewDonationRelativeLayout.setVisibility(View.VISIBLE);
        viewDonationRelativeLayout.animate().translationX(0).scaleX(1f).scaleY(1f).alpha(1f).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewDonationRelativeLayout.setTranslationX(0);
                viewDonationRelativeLayout.setScaleX(1f);
                viewDonationRelativeLayout.setScaleY(1f);
                viewDonationRelativeLayout.setAlpha(1f);
                viewDonationRelativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        collapseBottomNavButtons();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideViewDonationPart();
            }
        });

        uploaderTextView.setText(donationItemToBeViewed.getUploaderName());

        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagDonatedItem();
            }
        });

        titleAndWeightTextView.setText(donationItemToBeViewed.getItemName());
        mainImageImageView.setImageBitmap(donationItemToBeViewed.getItemImages().get(0).getImageBitmap());

        //sets the small images
        try {
            Bitmap image2 = donationItemToBeViewed.getItemImages().get(1).getImageBitmap();
            Bitmap image3 = donationItemToBeViewed.getItemImages().get(2).getImageBitmap();
            Bitmap image4 = donationItemToBeViewed.getItemImages().get(3).getImageBitmap();

            if(image2!=null) smallImage1.setImageBitmap(image2);
            if(image3!=null) smallImage2.setImageBitmap(image3);
            if(image4!=null) smallImage3.setImageBitmap(image4);

        }catch (Exception e){
            e.printStackTrace();
        }

        viewImagesRelativeLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDonationImages();
            }
        });

        donationDetailsTextV.setText(donationItemToBeViewed.getItemDetails());
        formalTimeTextView.setText(String.format("%s %s", getResources().getString(R.string.donation_listed_on), donationItemToBeViewed.getTimeOfCreation().getOfficialDate()));
        informalTimeTextView.setText(donationItemToBeViewed.getTimeOfCreation().getCasualDate());
        locationDescriptionTextView.setText(String.format("%s %s", getResources().getString(R.string.just_near), donationItemToBeViewed.getItemLocation().getAreaDescription()));

        openMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapForLocation();
            }
        });

        requestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestDonationActivity.class);
                Variables.selectedDonationItem = donationItemToBeViewed;
                startActivity(intent);
            }
        });
    }


    private void openMapForLocation(){
        mapRelativeLayout.setVisibility(View.VISIBLE);
        isMapOpen = true;

        viewDonationRelativeLayout.animate().translationX(0.9f).translationY(0.9f).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).start();

        collapsedNavButtonsLinearLayout.animate().translationY(Utils.dpToPx(40)).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).start();

        mapContainer.animate().alpha(1f).translationY(0).scaleX(1).scaleY(1).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mapContainer.setAlpha(1f);
                mapContainer.setTranslationY(0);
                mapContainer.setScaleX(1);
                mapContainer.setScaleY(1);

                viewDonationRelativeLayout.setTranslationX(0.9f);
                viewDonationRelativeLayout.setTranslationX(0.9f);

                collapsedNavButtonsLinearLayout.setTranslationY(Utils.dpToPx(40));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUESTCODE);
        }else{
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
        }

        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMap();
            }
        });
    }

    private void closeMap(){
        isMapOpen = false;

        viewDonationRelativeLayout.animate().translationX(1f).translationY(1f).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).start();

        collapsedNavButtonsLinearLayout.animate().translationY(0).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).start();

        mapContainer.animate().alpha(0f).translationY(Utils.dpToPx(100)).scaleX(0.9f).scaleY(0.9f).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mapContainer.setAlpha(0f);
                mapContainer.setTranslationY(Utils.dpToPx(100));
                mapContainer.setScaleX(0.9f);
                mapContainer.setScaleY(0.9f);
                mapRelativeLayout.setVisibility(View.GONE);

                viewDonationRelativeLayout.setTranslationX(1f);
                viewDonationRelativeLayout.setTranslationX(1f);

                collapsedNavButtonsLinearLayout.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled) {
            Toast.makeText(mContext,"Please turn on your GPS-Location.",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);
        googleMap.setOnMarkerClickListener(this);

        map.addMarker(new MarkerOptions().position(new LatLng(donationItemToBeViewed.getItemLocation().getLatitude(),
                donationItemToBeViewed.getItemLocation().getLongitude())).draggable(false));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(donationItemToBeViewed.getItemLocation().getLatitude(),
                donationItemToBeViewed.getItemLocation().getLongitude()), ZOOM));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try{
                    map.setMyLocationEnabled(true);
                }catch (SecurityException e){
                    e.printStackTrace();
                }
                map.setOnMyLocationButtonClickListener(this);
                map.setOnMyLocationClickListener(this);
            }
        }
    }


    private void openDonationImages() {

    }

    private void flagDonatedItem() {

    }

    private void hideViewDonationPart(){
        isViewDonationLayoutVisible = false;

        viewDonationRelativeLayout.animate().translationX(Utils.dpToPx(200)).scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewDonationRelativeLayout.setTranslationX(Utils.dpToPx(200));
                viewDonationRelativeLayout.setScaleX(0.8f);
                viewDonationRelativeLayout.setScaleY(0.8f);
                viewDonationRelativeLayout.setAlpha(0f);
                viewDonationRelativeLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        expandBottomNavButtons();
    }

    private void collapseBottomNavButtons(){
        isBottomNavButtonsLayoutVisible = false;

        bottomNavButtonsLinearLayout.setVisibility(View.GONE);
        collapsedNavButtonsLinearLayout.setVisibility(View.VISIBLE);
        collapsedImagesLinearLayout.animate().scaleX(0.6f).setDuration(mAnimationTime).setInterpolator(new LinearOutSlowInInterpolator()).start();
        collapsedNavButtonsLinearLayout.animate().scaleY(0.6f).setDuration(mAnimationTime).setInterpolator(new LinearOutSlowInInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        collapsedImagesLinearLayout.setScaleX(0.6f);
                        collapsedNavButtonsLinearLayout.setScaleY(0.6f);

                        bottomNavButtonsLinearLayout.setVisibility(View.GONE);
                        collapsedNavButtonsLinearLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();

    }

    private void expandBottomNavButtons(){
        isBottomNavButtonsLayoutVisible = true;

        collapsedImagesLinearLayout.animate().scaleX(1f).setDuration(mAnimationTime).setInterpolator(new LinearOutSlowInInterpolator()).start();
        collapsedNavButtonsLinearLayout.animate().scaleY(1f).setDuration(mAnimationTime).setInterpolator(new LinearOutSlowInInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        collapsedImagesLinearLayout.setScaleX(1f);
                        collapsedNavButtonsLinearLayout.setScaleY(1f);

                        bottomNavButtonsLinearLayout.setVisibility(View.VISIBLE);
                        collapsedNavButtonsLinearLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }

    @Override
    public void onBackPressed(){
        if(isMapOpen){
            closeMap();
        }else {
            if (isViewDonationLayoutVisible) {
                backButton.performClick();
            } else {
                super.onBackPressed();
            }
        }
    }
}
