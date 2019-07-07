package com.bry.firedonor.Activities;

import android.Manifest;
import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.firedonor.Constants;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.MyTime;
import com.bry.firedonor.Models.RequestItem;
import com.bry.firedonor.Models.SetLocation;
import com.bry.firedonor.R;
import com.bry.firedonor.Services.DatabaseManager;
import com.bry.firedonor.Services.Utils;
import com.bry.firedonor.Variables;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RequestDonationActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener
        ,GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener{
    private final String TAG = RequestDonationActivity.class.getSimpleName();
    private Context mContext;
    private DonationItem mDonationItemRequested;

    @Bind(R.id.newRequestLinearLayout) LinearLayout newRequestLinearLayout;
    @Bind(R.id.progressBarRelativeLayout) RelativeLayout progressBarRelativeLayout;
    private boolean isShowingSpinner = false;

    @Bind(R.id.backImageView) ImageView backImageView;
    @Bind(R.id.donationItemImageView) ImageView donationItemImageView;
    @Bind(R.id.donationTitleTextView) TextView donationTitleTextView;
    @Bind(R.id.listingDateTextView) TextView listingDateTextView;

    @Bind(R.id.organisationNameLinearLayout) LinearLayout organisationNameLinearLayout;
    private boolean isOrganisationNameLayoutVisible = true;
    @Bind(R.id.organisationNameEditText) EditText organisationNameEditText;
    @Bind(R.id.organisationContinueButton) Button organisationContinueButton;
    private String setOrganisationName = "";

    @Bind(R.id.organisationLocationLinearLayout) LinearLayout organisationLocationLinearLayout;
    private boolean isOrganisationLocationLayoutVisible = false;
    @Bind(R.id.openMapView) ImageView openMapView;
    private SetLocation mSetLocation;
    @Bind(R.id.locationContinueButton) Button locationContinueButton;
    private SetLocation setLocation;
    private boolean isAtSetLocationPart = false;
    private GoogleMap map;
    private double CBD_LAT = -1.2805;
    private double CBD_LONG = 36.8163;
    private LatLng CBD = new LatLng(CBD_LAT, CBD_LONG);
    private int ZOOM = 15;
    private Marker mySetMarker;
    private final int REQUESTCODE = 3301;
    private GoogleApiClient mGoogleApiClient;
    MapFragment mapFragment;
    PlaceAutocompleteFragment autocompleteFragment;
    @Bind(R.id.mapRelativeLayout) RelativeLayout mapRelativeLayout;
    @Bind(R.id.mapContainer) CardView mapContainer;
    @Bind(R.id.setLocationButton) Button setLocationButton;
    private boolean isMapOpen = false;

    @Bind(R.id.pickUpTimeLinearLayout) LinearLayout pickUpTimeLinearLayout;
    private boolean isPicUpTimeLayoutVisible = false;
    @Bind(R.id.openCalendarView) ImageView openCalendarView;
    public MyTime setTime;
    @Bind(R.id.calendarContinueBtn) Button calendarContinueBtn;

    @Bind(R.id.vehicleExpectedLinearLayout) LinearLayout vehicleExpectedLinearLayout;
    private boolean isExpectedVehicleLayoutVisible = false;
    @Bind(R.id.vehicleDescriptionEditText) EditText vehicleDescriptionEditText;
    private String vehicleDescription = "";
    @Bind(R.id.vehicleNumberPlate) EditText vehicleNumberPlate;
    private String vehiclePlate = "";
    @Bind(R.id.vehicleContinueButton) Button vehicleContinueButton;

    @Bind(R.id.confirmDetailsLinearLayout) LinearLayout confirmDetailsLinearLayout;
    private boolean isAtConfirmDetails = false;
    @Bind(R.id.confirmName) TextView confirmName;
    @Bind(R.id.confirmLocation) TextView confirmLocation;
    @Bind(R.id.confirmPickUpDate) TextView confirmPickUpDate;
    @Bind(R.id.confirmVehicle) TextView confirmVehicle;
    @Bind(R.id.sendRequestButton) Button sendRequestButton;

    @Bind(R.id.finishedUpLinearLayout) LinearLayout finishedUpLinearLayout;
    private boolean isAtFinishedUpPart = false;
    @Bind(R.id.finishedUpPartFinishButton) Button finishedUpPartFinishButton;

    private int mAnimationDuration = 300;
    private int transitionOutTranslation = Utils.dpToPx(-200);
    private int transitionInTranslation = Utils.dpToPx(400);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_donation);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mDonationItemRequested = Variables.selectedDonationItem;

        donationItemImageView.setImageBitmap(mDonationItemRequested.getItemImages().get(0).getImageBitmap());
        donationTitleTextView.setText(mDonationItemRequested.getItemName());
        listingDateTextView.setText(String.format("%s %s", getResources().getString(R.string.listed_on),
                mDonationItemRequested.getTimeOfCreation().getOfficialDate()));
        backImageView.setOnClickListener(this);
        loadOrganisationNameLayout();
    }

    private void loadOrganisationNameLayout() {
        isOrganisationNameLayoutVisible = true;
        organisationNameLinearLayout.setVisibility(View.VISIBLE);

        organisationNameLinearLayout.animate().translationX(0).alpha(1f).setDuration(mAnimationDuration)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                organisationNameLinearLayout.setTranslationX(0);
                organisationNameLinearLayout.setAlpha(1f);
                organisationNameLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        organisationContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrganisationName = organisationNameEditText.getText().toString().trim();
                if(setOrganisationName.equals(""))organisationNameEditText.setError(getResources().getString(R.string.you_need_to_set_a_name));
                else{
                    organisationNameLinearLayout.animate().translationX(transitionOutTranslation).alpha(0f).setDuration(mAnimationDuration)
                            .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            organisationNameLinearLayout.setTranslationX(transitionOutTranslation);
                            organisationNameLinearLayout.setAlpha(0f);
                            organisationNameLinearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                    isOrganisationNameLayoutVisible = false;
                    loadLocationLayout();
                }
            }
        });
    }

    private void loadLocationLayout(){
        isOrganisationLocationLayoutVisible = true;
        organisationLocationLinearLayout.setVisibility(View.VISIBLE);

        organisationLocationLinearLayout.animate().translationX(0).alpha(1f).setDuration(mAnimationDuration)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                organisationLocationLinearLayout.setTranslationX(0);
                organisationLocationLinearLayout.setAlpha(1f);
                organisationLocationLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        openMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        locationContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSetLocation!=null){
                    organisationLocationLinearLayout.animate().translationX(transitionOutTranslation).alpha(0f).setDuration(mAnimationDuration)
                            .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            organisationLocationLinearLayout.setTranslationX(transitionOutTranslation);
                            organisationLocationLinearLayout.setAlpha(0f);
                            organisationLocationLinearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                    isOrganisationLocationLayoutVisible= false;
                    loadCalendarLayout();
                }else{
                    Snackbar.make(findViewById(R.id.requestDonationCoordinatorLayout),
                            getResources().getString(R.string.please_set_a_location_by_clicking_the_map_icon),Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openMap(){
        mapRelativeLayout.setVisibility(View.VISIBLE);
        isMapOpen = true;

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RequestDonationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUESTCODE);
        }else{
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
        }

        mapContainer.animate().alpha(1f).translationY(0).scaleX(1).scaleY(1).setDuration(mAnimationDuration)
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
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setLocation!=null){
                    closeMap();
                }else Toast.makeText(mContext,getResources().getString(R.string.set_the_location_of_the_donated_items),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void closeMap(){
        isMapOpen = false;
        mapContainer.animate().alpha(0f).translationY(Utils.dpToPx(100)).scaleX(0.9f).scaleY(0.9f).setDuration(mAnimationDuration)
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
        marker.remove();
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
        if(mySetMarker!=null)mySetMarker.remove();
        mySetMarker = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).draggable(true));
        setLocation = new SetLocation(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);
        googleMap.setOnMarkerClickListener(this);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CBD, ZOOM));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mySetMarker!=null)mySetMarker.remove();
                mySetMarker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                setLocation = new SetLocation(latLng);
            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });

        LatLng botBnd = new LatLng(-4.716667, 27.433333);
        LatLng topBnd = new LatLng(4.883333, 41.8583834826426);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setBoundsBias(new LatLngBounds(botBnd,topBnd));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                LatLng searchedPlace = place.getLatLng();
                if(mySetMarker!=null) mySetMarker.remove();

                mySetMarker = map.addMarker(new MarkerOptions().position(searchedPlace).draggable(true));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedPlace, ZOOM));

                setLocation = new SetLocation(searchedPlace);
                setLocation.setAreaDescription(place.getAddress().toString());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
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


    private void loadCalendarLayout(){
        isPicUpTimeLayoutVisible = true;
        pickUpTimeLinearLayout.setVisibility(View.VISIBLE);

        pickUpTimeLinearLayout.animate().translationX(0).alpha(1f).setDuration(mAnimationDuration)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pickUpTimeLinearLayout.setTranslationX(0);
                pickUpTimeLinearLayout.setAlpha(1f);
                pickUpTimeLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        openCalendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });

        calendarContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime = Variables.selectedTime;
                if(setTime!=null){
                    pickUpTimeLinearLayout.animate().translationX(transitionOutTranslation).alpha(0f).setDuration(mAnimationDuration)
                            .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            pickUpTimeLinearLayout.setTranslationX(transitionOutTranslation);
                            pickUpTimeLinearLayout.setAlpha(0f);
                            pickUpTimeLinearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                    isPicUpTimeLayoutVisible= false;
                    loadExpectedVehicleLayout();
                }else{
                    Snackbar.make(findViewById(R.id.requestDonationCoordinatorLayout),
                            getResources().getString(R.string.please_set_a_date_by_clicking_the_date_icon),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openCalendar() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR,year);
            cal.set(Calendar.MONTH,month);
            cal.set(Calendar.DAY_OF_MONTH,day);

            Variables.selectedTime = new MyTime(cal);
        }
    }


    private void loadExpectedVehicleLayout(){
        isExpectedVehicleLayoutVisible = true;
        vehicleExpectedLinearLayout.setVisibility(View.VISIBLE);

        vehicleExpectedLinearLayout.animate().translationX(0).alpha(1f).setDuration(mAnimationDuration)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                vehicleExpectedLinearLayout.setTranslationX(0);
                vehicleExpectedLinearLayout.setAlpha(1f);
                vehicleExpectedLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        vehicleContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicleDescription = vehicleDescriptionEditText.getText().toString().trim();
                vehiclePlate = vehicleNumberPlate.getText().toString().toUpperCase().trim();

                if(vehicleDescription.equals(""))vehicleDescriptionEditText.setError(getResources().getString(R.string.a_description_is_needed));
                else if(vehiclePlate.equals(""))vehicleNumberPlate.setError(getResources().getString(R.string.a_number_plate_is_needed));
                else{
                    vehicleExpectedLinearLayout.animate().translationX(transitionOutTranslation).alpha(0f).setDuration(mAnimationDuration)
                            .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            vehicleExpectedLinearLayout.setTranslationX(transitionOutTranslation);
                            vehicleExpectedLinearLayout.setAlpha(0f);
                            vehicleExpectedLinearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                    isExpectedVehicleLayoutVisible= false;
                    loadConfirmDetailsPart();
                }
            }
        });
    }

    private void loadConfirmDetailsPart() {
        isAtConfirmDetails = true;
        confirmDetailsLinearLayout.setVisibility(View.VISIBLE);

        confirmDetailsLinearLayout.animate().translationX(0).alpha(1f).setDuration(mAnimationDuration)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                confirmDetailsLinearLayout.setTranslationX(0);
                confirmDetailsLinearLayout.setAlpha(1f);
                confirmDetailsLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        confirmName.setText(setOrganisationName);
        confirmLocation.setText(mSetLocation.getAreaDescription());
        confirmPickUpDate.setText(setTime.getOfficialDate());
        confirmVehicle.setText(vehicleDescription+": "+vehiclePlate);

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShowingSpinner)sendRequest();
            }
        });
    }

    private void sendRequest() {
        showLoadingScreens();
        RequestItem myRequest = new RequestItem(setOrganisationName,mSetLocation,setTime,vehicleDescription,vehiclePlate);
        myRequest.setDonationId(mDonationItemRequested.getItemId());
        myRequest.setRequesterUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String SUCCESS = "SUCCESS";
        new DatabaseManager(mContext,SUCCESS).uploadDonationRequest(myRequest);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideLoadingScreens();
                confirmDetailsLinearLayout.animate().alpha(0f).translationX(transitionOutTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                confirmDetailsLinearLayout.setTranslationX(transitionOutTranslation);
                                confirmDetailsLinearLayout.setAlpha(0f);
                                confirmDetailsLinearLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                isAtConfirmDetails = false;
                loadSuccessfulUploadPart();
            }
        },new IntentFilter(SUCCESS));
    }

    private void loadSuccessfulUploadPart() {
        isAtFinishedUpPart = true;
        finishedUpLinearLayout.setVisibility(View.VISIBLE);

        finishedUpLinearLayout.animate().translationX(0).alpha(1f).setDuration(mAnimationDuration)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finishedUpLinearLayout.setTranslationX(0);
                finishedUpLinearLayout.setAlpha(1f);
                finishedUpLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        finishedUpPartFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAtFinishedUpPart = false;
                finish();
            }
        });
    }

    private void showLoadingScreens(){
        progressBarRelativeLayout.setVisibility(View.VISIBLE);
        newRequestLinearLayout.setAlpha(Constants.LOADING_SCREEN_BACKGROUND_ALPHA);
        isShowingSpinner = true;
    }

    private void hideLoadingScreens(){
        isShowingSpinner = false;
        progressBarRelativeLayout.setVisibility(View.GONE);
        newRequestLinearLayout.setAlpha(1f);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(backImageView)){
            if(!isShowingSpinner){
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isMapOpen){
            closeMap();
        }else if(isOrganisationLocationLayoutVisible){
            organisationLocationLinearLayout.animate().translationX(transitionInTranslation).alpha(0f).setDuration(mAnimationDuration)
                    .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    organisationLocationLinearLayout.setTranslationX(transitionInTranslation);
                    organisationLocationLinearLayout.setAlpha(0f);
                    organisationLocationLinearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
            isOrganisationLocationLayoutVisible= false;
            loadOrganisationNameLayout();
        }else if(isPicUpTimeLayoutVisible){
            pickUpTimeLinearLayout.animate().translationX(transitionInTranslation).alpha(0f).setDuration(mAnimationDuration)
                    .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    pickUpTimeLinearLayout.setTranslationX(transitionInTranslation);
                    pickUpTimeLinearLayout.setAlpha(0f);
                    pickUpTimeLinearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
            isPicUpTimeLayoutVisible= false;
            loadLocationLayout();
        }else if(isExpectedVehicleLayoutVisible){
            vehicleExpectedLinearLayout.animate().translationX(transitionInTranslation).alpha(0f).setDuration(mAnimationDuration)
                    .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    vehicleExpectedLinearLayout.setTranslationX(transitionInTranslation);
                    vehicleExpectedLinearLayout.setAlpha(0f);
                    vehicleExpectedLinearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
            isExpectedVehicleLayoutVisible= false;
            loadCalendarLayout();
        }else if(isAtConfirmDetails){
            confirmDetailsLinearLayout.animate().translationX(transitionInTranslation).alpha(0f).setDuration(mAnimationDuration)
                    .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    confirmDetailsLinearLayout.setTranslationX(transitionInTranslation);
                    confirmDetailsLinearLayout.setAlpha(0f);
                    confirmDetailsLinearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
            isAtConfirmDetails= false;
            loadExpectedVehicleLayout();
        }else{
            super.onBackPressed();
        }
    }
}
