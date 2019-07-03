package com.bry.firedonor.Activities;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bry.firedonor.Adapters.ConfirmImageAdapter;
import com.bry.firedonor.Adapters.SelectedImageAdapter;
import com.bry.firedonor.Constants;
import com.bry.firedonor.Models.DonationImage;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.SetLocation;
import com.bry.firedonor.R;
import com.bry.firedonor.Services.DatabaseManager;
import com.bry.firedonor.Services.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewDonationActivity extends AppCompatActivity {
    private final String TAG = NewDonationActivity.class.getSimpleName();
    private Context mContext;

    @Bind(R.id.newDonationContainerLinearLayout) LinearLayout newDonationContainerLinearLayout;
    @Bind(R.id.progressBarRelativeLayout) RelativeLayout progressBarRelativeLayout;
    private boolean isShowingSpinner = false;

    @Bind(R.id.backButton) ImageButton backButton;
    @Bind(R.id.beginLinearLayout) Button beginLinearLayout;
    @Bind(R.id.beginButton) Button beginButton;
    private boolean isAtBeginningPart = true;

    @Bind(R.id.itemNameLinearLayout) LinearLayout itemNameLinearLayout;
    @Bind(R.id.nameAndMassEditText) EditText nameAndMassEditText;
    @Bind(R.id.nameAndMassContinueButton) Button nameAndMassContinueButton;
    @Bind(R.id.nameAndMassBackButton) ImageView nameAndMassBackButton;
    private boolean isAtItemNamePart = false;
    private String mNameAndMassText = "";

    @Bind(R.id.itemImagesLayout) LinearLayout itemImagesLayout;
    @Bind(R.id.selectedPhotosRecyclerView) RecyclerView selectedPhotosRecyclerView;
    @Bind(R.id.addAPhotoLinearLayout) LinearLayout addAPhotoLinearLayout;
    @Bind(R.id.takeAPhotoLinearLayout) LinearLayout takeAPhotoLinearLayout;
    @Bind(R.id.takePhotoBackButton) ImageView takePhotoBackButton;
    @Bind(R.id.takePhotoContinueButton) Button takePhotoContinueButton;
    private boolean isAtItemImagesPart = false;
    private List<DonationImage> selectedDonationItemImages = new ArrayList<>();
    private final int PICK_IMAGE_REQUEST = 1012;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri mFilepath;

    @Bind(R.id.itemDetailsLinearLayout) LinearLayout itemDetailsLinearLayout;
    @Bind(R.id.itemDetailsEditText) EditText itemDetailsEditText;
    @Bind(R.id.itemDetailsBackButton) ImageView itemDetailsBackButton;
    @Bind(R.id.itemDetailsContinueButton) Button itemDetailsContinueButton;
    private String setItemDetails = "";
    private boolean isAtItemDetailsPart = false;

    @Bind(R.id.openMapLayout) LinearLayout openMapLayout;
    @Bind(R.id.setLocationBackButton) ImageButton setLocationBackButton;
    @Bind(R.id.setLocationContinueButton) Button setLocationContinueButton;
    private SetLocation setLocation;
    private boolean isAtSetLocationPart = false;

    @Bind(R.id.confirmDetailsLinearLayout) LinearLayout confirmDetailsLinearLayout;
    @Bind(R.id.confirmMainImage) ImageView confirmMainImage;
    @Bind(R.id.confirmImagesRecyclerView) RecyclerView confirmImagesRecyclerView;
    @Bind(R.id.confirmLocationTextView) TextView confirmLocationTextView;
    @Bind(R.id.donationDetailsTextView) TextView donationDetailsTextView;
    @Bind(R.id.confirmDetailsBackButton) ImageView confirmDetailsBackButton;
    @Bind(R.id.confirmDetailFinishButton) Button confirmDetailFinishButton;
    private boolean isAtConfirmDetailsPart = false;

    @Bind(R.id.uploadedLinearLayout) LinearLayout uploadedLinearLayout;
    @Bind(R.id.finishUpButton) Button finishUpButton;
    private boolean isAtFinishedUpPart = false;

    private int mAnimationDuration = 300;
    private int transitionOutTranslation =Utils.dpToPx(-200);
    private int transitionInTranslation = Utils.dpToPx(400);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_donation);

        ButterKnife.bind(this);
        mContext = getApplicationContext();

        setBeginningPart();
    }

    private void setBeginningPart() {
        beginLinearLayout.setVisibility(View.VISIBLE);
        isAtBeginningPart = true;
        beginLinearLayout.animate().alpha(1f).translationX(0).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                beginLinearLayout.setTranslationX(0);
                beginLinearLayout.setAlpha(1f);
                beginLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginLinearLayout.animate().alpha(0f).translationX(transitionOutTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                beginLinearLayout.setTranslationX(transitionOutTranslation);
                                beginLinearLayout.setAlpha(0f);
                                beginLinearLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
               isAtBeginningPart = false;
                setItemNamePart();
            }
        });
    }

    private void setItemNamePart() {
        isAtItemNamePart = true;
        itemNameLinearLayout.setVisibility(View.VISIBLE);

        itemNameLinearLayout.animate().alpha(1f).translationX(0).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        itemNameLinearLayout.setTranslationX(0);
                        itemNameLinearLayout.setAlpha(1f);
                        itemNameLinearLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();

        nameAndMassContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameAndMassText = nameAndMassEditText.getText().toString().trim();
                if(mNameAndMassText.equals("")) nameAndMassEditText.setError("We'll need this.");
                else{
                    itemNameLinearLayout.animate().alpha(0f).translationX(transitionOutTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    itemNameLinearLayout.setTranslationX(transitionOutTranslation);
                                    itemNameLinearLayout.setAlpha(0f);
                                    itemNameLinearLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                    isAtItemNamePart = false;
                    setImagesPart();
                }
            }
        });
        nameAndMassBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemNameLinearLayout.animate().translationX(transitionInTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                itemNameLinearLayout.setTranslationX(transitionInTranslation);
                                itemNameLinearLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                isAtItemNamePart = false;
                setItemNamePart();
            }
        });
    }

    private void setImagesPart() {
        isAtItemImagesPart = true;
        itemImagesLayout.setVisibility(View.VISIBLE);

        itemImagesLayout.animate().alpha(1f).translationX(0).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        itemImagesLayout.setTranslationX(0);
                        itemImagesLayout.setAlpha(1f);
                        itemImagesLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();

        addAPhotoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDonationItemImages.size()< Constants.SELECTED_ITEMS_SPAN_COUNT) pickPhotoFromStorage();
                else Snackbar.make(findViewById(R.id.newDonationActivitylayout), getResources().getString(R.string.max_images),Snackbar.LENGTH_SHORT).show();
            }
        });
        takeAPhotoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDonationItemImages.size()<Constants.SELECTED_ITEMS_SPAN_COUNT) pickPhotoFromCamera();
                else Snackbar.make(findViewById(R.id.newDonationActivitylayout),getResources().getString(R.string.max_images),Snackbar.LENGTH_SHORT).show();
            }
        });

        takePhotoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemImagesLayout.animate().translationX(transitionInTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                itemImagesLayout.setTranslationX(transitionInTranslation);
                                itemImagesLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                setItemNamePart();
                isAtItemImagesPart = false;
            }
        });

        takePhotoContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDonationItemImages.isEmpty()){
                    Snackbar.make(findViewById(R.id.newDonationActivitylayout), getResources().getString(R.string.add_at_least_one_image),Snackbar.LENGTH_SHORT).show();
                }else{
                    itemImagesLayout.animate().alpha(0f).translationX(transitionOutTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    itemImagesLayout.setTranslationX(transitionOutTranslation);
                                    itemImagesLayout.setAlpha(0f);
                                    itemImagesLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                    loadItemDetailsPart();
                    isAtItemImagesPart = false;
                }
            }
        });
        reloadSelectedPhotosList();
    }

    private void pickPhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void reloadSelectedPhotosList() {
        SelectedImageAdapter adapter = new SelectedImageAdapter(selectedDonationItemImages, NewDonationActivity.this, mContext);
        selectedPhotosRecyclerView.setAdapter(adapter);
        selectedPhotosRecyclerView.setLayoutManager(new GridLayoutManager(mContext,Constants.SELECTED_ITEMS_SPAN_COUNT));
    }

    private void pickPhotoFromStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                mFilepath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilepath);
                    Bitmap bm = getResizedBitmap(bitmap,1500);
                    DonationImage donImage = new DonationImage();
                    donImage.setImageBitmap(bm);

                    selectedDonationItemImages.add(donImage);
                    reloadSelectedPhotosList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try{
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                Bitmap bm = getResizedBitmap(bitmap,1500);
                DonationImage donImage = new DonationImage();
                donImage.setImageBitmap(bm);

                selectedDonationItemImages.add(donImage);
                reloadSelectedPhotosList();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }



    private void loadItemDetailsPart() {
        isAtItemDetailsPart = true;
        itemDetailsLinearLayout.setVisibility(View.VISIBLE);

        itemDetailsLinearLayout.animate().alpha(1f).translationX(0).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        itemDetailsLinearLayout.setTranslationX(0);
                        itemDetailsLinearLayout.setAlpha(1f);
                        itemDetailsLinearLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();

        itemDetailsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetailsLinearLayout.animate().translationX(transitionInTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                itemDetailsLinearLayout.setTranslationX(transitionInTranslation);
                                itemDetailsLinearLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                setImagesPart();
                isAtItemDetailsPart = false;
            }
        });

        itemDetailsContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemDetails = itemDetailsEditText.getText().toString().trim();
                if(setItemDetails.equals("")) Snackbar.make(findViewById(R.id.newDonationActivitylayout),
                        getResources().getString(R.string.details_needed),Snackbar.LENGTH_SHORT).show();
                else{
                    isAtItemDetailsPart = false;
                    itemDetailsLinearLayout.animate().alpha(0f).translationX(transitionOutTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    itemDetailsLinearLayout.setTranslationX(transitionOutTranslation);
                                    itemDetailsLinearLayout.setAlpha(0f);
                                    itemDetailsLinearLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                    setLocationPart();
                }

            }
        });


    }

    private void setLocationPart(){
        isAtSetLocationPart = true;
        if(setLocation!=null) setLocationContinueButton.setText(getResources().getString(R.string.Continue));
        else setLocationContinueButton.setText(getResources().getString(R.string.open_map));
        openMapLayout.animate().alpha(1f).translationX(0).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        openMapLayout.setTranslationX(0);
                        openMapLayout.setAlpha(1f);
                        openMapLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();

        setLocationBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapLayout.animate().translationX(transitionInTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                openMapLayout.setTranslationX(transitionInTranslation);
                                openMapLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                loadItemDetailsPart();
                isAtSetLocationPart = false;
            }
        });

        setLocationContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setLocation!=null){
                    isAtSetLocationPart = false;
                    openMapLayout.animate().alpha(0f).translationX(transitionOutTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    openMapLayout.setTranslationX(transitionOutTranslation);
                                    openMapLayout.setAlpha(0f);
                                    openMapLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                    setConfirmDetailsPart();
                }else{
                    openMapToSetLocation();
                }
            }
        });

    }

    private void openMapToSetLocation(){
        //add this when location is set
//        if(setLocation!=null) setLocationContinueButton.setText(getResources().getString(R.string.Continue));
//        else setLocationContinueButton.setText(getResources().getString(R.string.open_map));

    }

    private void setConfirmDetailsPart(){
        isAtConfirmDetailsPart = true;
        confirmDetailsLinearLayout.animate().alpha(1f).translationX(0).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
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

        if(!setLocation.getAreaDescription().equals(""))confirmLocationTextView
                .setText(String.format("%s%s", getResources().getString(R.string.location_area), setLocation.getAreaDescription()));

        donationDetailsTextView.setText(setItemDetails);

        confirmDetailsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShowingSpinner) {
                    confirmDetailsLinearLayout.animate().translationX(transitionInTranslation).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    confirmDetailsLinearLayout.setTranslationX(transitionInTranslation);
                                    confirmDetailsLinearLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                    setLocationPart();
                    isAtConfirmDetailsPart = false;
                }
            }
        });

        confirmDetailFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShowingSpinner) {
                    prepDonationItemForUpload();
                }
            }
        });

        DonationImage mainImage = selectedDonationItemImages.get(0);
        List<DonationImage> otherImages = new ArrayList<>();
        for(int i=1; i<selectedDonationItemImages.size();i++) otherImages.add(selectedDonationItemImages.get(i));

        ConfirmImageAdapter adapter = new ConfirmImageAdapter(otherImages, NewDonationActivity.this, mContext);
        confirmImagesRecyclerView.setAdapter(adapter);
        int span = Constants.SELECTED_ITEMS_SPAN_COUNT-1;
        confirmImagesRecyclerView.setLayoutManager(new GridLayoutManager(mContext,span));

        confirmMainImage.setImageBitmap(mainImage.getImageBitmap());

    }

    private void prepDonationItemForUpload() {
        if(!isOnline()){
            Snackbar.make(findViewById(R.id.newDonationActivitylayout),getResources().getString(R.string.no_internet_connection),Snackbar.LENGTH_SHORT).show();
        }else{
            showLoadingScreens();
            LongOperationBakeThoseGodDamnImages operationBakeThoseGodDamnImages = new LongOperationBakeThoseGodDamnImages();
            operationBakeThoseGodDamnImages.execute("");

        }
    }

    private class LongOperationBakeThoseGodDamnImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            for(DonationImage imageItem:selectedDonationItemImages){
                imageItem.setEncodedImageString(encodeBitmapForFirebaseStorage(imageItem.getImageBitmap()));
                imageItem.setImageBitmap(null);
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            createUploadObjectAndUploadToFirebase();
        }

    }

    private String encodeBitmapForFirebaseStorage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }


    private void createUploadObjectAndUploadToFirebase(){
        DonationItem item = new DonationItem(mNameAndMassText,selectedDonationItemImages,selectedDonationItemImages.get(0).getImageId(),setItemDetails,setLocation);
        String SUCCESSFUL_UPLOAD = "SUCCESSFUL_UPLOAD";
        String FAILED_UPLOAD = "FAILED_UPLOAD";
        new DatabaseManager(mContext,SUCCESSFUL_UPLOAD).uploadDonationItem(item);

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
                isAtConfirmDetailsPart = false;
                loadSuccessfulUploadPart();
            }
        },new IntentFilter(SUCCESSFUL_UPLOAD));
    }

    private void showLoadingScreens(){
        progressBarRelativeLayout.setVisibility(View.VISIBLE);
        newDonationContainerLinearLayout.setAlpha(Constants.LOADING_SCREEN_BACKGROUND_ALPHA);
        isShowingSpinner = true;
    }

    private void hideLoadingScreens(){
        isShowingSpinner = false;
        progressBarRelativeLayout.setVisibility(View.GONE);
        newDonationContainerLinearLayout.setAlpha(1f);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void loadSuccessfulUploadPart(){
        isAtFinishedUpPart = true;
        uploadedLinearLayout.animate().alpha(1f).translationX(0).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(mAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
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

        finishUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAtFinishedUpPart = false;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(!isShowingSpinner){
            if(isAtItemNamePart)nameAndMassBackButton.performClick();
            else if(isAtItemImagesPart) takePhotoBackButton.performClick();
            else if(isAtItemDetailsPart)itemDetailsBackButton.performClick();
            else if(isAtSetLocationPart)setLocationBackButton.performClick();
            else if(isAtConfirmDetailsPart) confirmDetailsBackButton.performClick();
            else super.onBackPressed();
        }else{
            super.onBackPressed();
        }
    }

}
/*
* To-do: Make camera load in activity
* */