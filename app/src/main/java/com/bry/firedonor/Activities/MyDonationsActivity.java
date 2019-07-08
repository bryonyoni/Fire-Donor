package com.bry.firedonor.Activities;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.Toast;

import com.bry.firedonor.Adapters.ListedDonationItemAdapter;
import com.bry.firedonor.Adapters.MyDonationsAdapter;
import com.bry.firedonor.Constants;
import com.bry.firedonor.Models.DonationImage;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.Models.RequestItem;
import com.bry.firedonor.R;
import com.bry.firedonor.Services.DatabaseManager;
import com.bry.firedonor.Services.Utils;
import com.bry.firedonor.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyDonationsActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    @Bind(R.id.backButton) ImageView backButton;
    @Bind(R.id.mainLinearLayout) LinearLayout mainLinearLayout;
    @Bind(R.id.progressbarRelativeLayout) RelativeLayout progressbarRelativeLayout;
    private boolean isShowingLoadingScreens = false;
    private List<DonationItem> loadedDonationItems = new ArrayList<>();
    @Bind(R.id.myDonationsRecyclerView) RecyclerView myDonationsRecyclerView;
    private MyDonationsAdapter myDonationsAdapter;

    private DonationItem selectedDonatedItem;
    private int mAnimationTime = 300;

    @Bind(R.id.editDonationRelativeLayout) RelativeLayout editDonationRelativeLayout;
    private boolean isEditPartOpen = false;
    @Bind(R.id.editBackButton) ImageView editBackButton;
    @Bind(R.id.deleteTextButton) TextView deleteTextButton;
    @Bind(R.id.addPhotoImageButton) ImageButton addPhotoImageButton;
    @Bind(R.id.takePhotoButton) ImageButton takePhotoButton;
    private final int PICK_IMAGE_REQUEST = 1012;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri mFilepath;
    @Bind(R.id.image1) ImageView image1;
    @Bind(R.id.image2) ImageView image2;
    @Bind(R.id.image3) ImageView image3;
    @Bind(R.id.image4) ImageView image4;
    @Bind(R.id.itemTitleTextView) TextView itemTitleTextView;
    @Bind(R.id.editName) ImageView editName;
    @Bind(R.id.editDetails) ImageView editDetails;

    @Bind(R.id.blackBack) RelativeLayout blackBack;
    private boolean isSetNewDataOpen = false;
    @Bind(R.id.editDetailsRelativeLayout) RelativeLayout editDetailsRelativeLayout;
    @Bind(R.id.nameAndMassEditText) EditText nameAndMassEditText;
    @Bind(R.id.itemDetailsEditText) EditText itemDetailsEditText;
    @Bind(R.id.finishedEditingButton) Button finishedEditingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);
        ButterKnife.bind(this);
        mContext = getApplicationContext();

        backButton.setOnClickListener(this);
        loadMyDonations();

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverForOpenEditDonation,
                new IntentFilter(Constants.EDIT_DONATION_INTENT_FILTER));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForOpenEditDonation);
    }

    private void loadMyDonations() {
        showLoadingScreens();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS).child(uid)
                .child(Constants.UPLOADED_DONATION_ITEMS);
        donationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                        DonationItem item = snap.getValue(DonationItem.class);
                        if(snap.child(item.getItemId()).child(Constants.UPLOADED_DONATION_REQUESTS).exists()){
                            List<RequestItem> myReceivedRequests = new ArrayList<>();
                            for(DataSnapshot requestSnap:snap.child(item.getItemId()).child(Constants.UPLOADED_DONATION_REQUESTS).getChildren()){
                                RequestItem it = requestSnap.getValue(RequestItem.class);
                                myReceivedRequests.add(it);
                            }
                            item.setDonationRequests(myReceivedRequests);
                        }
                        loadedDonationItems.add(item);
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
        LongOperationUnBakeThoseGodDamnImages operationUnBakeThoseGodDamnImages = new LongOperationUnBakeThoseGodDamnImages();
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
        myDonationsAdapter = new MyDonationsAdapter(loadedDonationItems, MyDonationsActivity.this, mContext);
        myDonationsRecyclerView.setAdapter(myDonationsAdapter);
        myDonationsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }

    @Override
    public void onClick(View v) {
        if(v.equals(backButton)){
            finish();
        }
    }

    private void showLoadingScreens(){
        isShowingLoadingScreens = true;
        mainLinearLayout.setVisibility(View.GONE);
        progressbarRelativeLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreens(){
        isShowingLoadingScreens = false;
        mainLinearLayout.setVisibility(View.VISIBLE);
        progressbarRelativeLayout.setVisibility(View.GONE);
    }

    BroadcastReceiver mMessageReceiverForOpenEditDonation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            openEditPart();
        }
    };

    private void openEditPart() {
        selectedDonatedItem = Variables.selectedDonationItem;
        isEditPartOpen = true;
        editDonationRelativeLayout.setVisibility(View.VISIBLE);
        editDonationRelativeLayout.animate().translationX(0).alpha(1f).scaleY(1f).scaleX(1f).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                editDonationRelativeLayout.setVisibility(View.VISIBLE);
                editDonationRelativeLayout.setTranslationX(0);
                editDonationRelativeLayout.setAlpha(1f);
                editDonationRelativeLayout.setScaleY(1f);
                editDonationRelativeLayout.setScaleX(1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        editBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEditPart();
            }
        });

        deleteTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDonation();
            }
        });

        addPhotoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDonatedItem.getItemImages().size()<Constants.SELECTED_ITEMS_SPAN_COUNT){
                    pickPhotoFromStorage();
                }else{
                    Toast.makeText(mContext,getResources().getString(R.string.max_images),Toast.LENGTH_SHORT).show();
                }
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDonatedItem.getItemImages().size()<Constants.SELECTED_ITEMS_SPAN_COUNT){
                    pickPhotoFromCamera();
                }else{
                    Toast.makeText(mContext,getResources().getString(R.string.max_images),Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            Bitmap imge0 = selectedDonatedItem.getItemImages().get(0).getImageBitmap();
            Bitmap imge2 = selectedDonatedItem.getItemImages().get(1).getImageBitmap();
            Bitmap imge3 = selectedDonatedItem.getItemImages().get(2).getImageBitmap();
            Bitmap imge4 = selectedDonatedItem.getItemImages().get(3).getImageBitmap();

            if(imge2!=null) image1.setImageBitmap(imge2);
            if(imge3!=null) image2.setImageBitmap(imge3);
            if(imge4!=null) image3.setImageBitmap(imge4);
            if(imge0!=null) image4.setImageBitmap(imge0);

        }catch (Exception e){
            e.printStackTrace();
        }

        itemTitleTextView.setText(selectedDonatedItem.getItemName());

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditor();
            }
        });

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditor();
            }
        });
    }


    private void pickPhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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
                    donImage.setImageId(Integer.toString(selectedDonatedItem.getItemImages().size()));

                    loadedDonationItems.get(loadedDonationItems.indexOf(selectedDonatedItem)).getItemImages().add(donImage);
                    selectedDonatedItem.getItemImages().add(donImage);
                    encodeImageThenStoreInFirebase(donImage);
                    refreshRecyclerView();

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
                donImage.setImageId(Integer.toString(selectedDonatedItem.getItemImages().size()));

                loadedDonationItems.get(loadedDonationItems.indexOf(selectedDonatedItem)).getItemImages().add(donImage);
                selectedDonatedItem.getItemImages().add(donImage);
                encodeImageThenStoreInFirebase(donImage);
                refreshRecyclerView();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void encodeImageThenStoreInFirebase(DonationImage dm){
        dm.setEncodedImageString(encodeBitmapForFirebaseStorage(dm.getImageBitmap()));
        dm.setImageBitmap(null);

        new DatabaseManager(mContext,"").updateImageDataInFirebase(selectedDonatedItem,dm);
    }

    private String encodeBitmapForFirebaseStorage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void refreshRecyclerView() {
        myDonationsAdapter = new MyDonationsAdapter(loadedDonationItems, MyDonationsActivity.this, mContext);
        myDonationsRecyclerView.setAdapter(myDonationsAdapter);
        myDonationsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        myDonationsRecyclerView.scrollToPosition(loadedDonationItems.indexOf(selectedDonatedItem));

        try {
            Bitmap imge0 = selectedDonatedItem.getItemImages().get(0).getImageBitmap();
            Bitmap imge2 = selectedDonatedItem.getItemImages().get(1).getImageBitmap();
            Bitmap imge3 = selectedDonatedItem.getItemImages().get(2).getImageBitmap();
            Bitmap imge4 = selectedDonatedItem.getItemImages().get(3).getImageBitmap();

            if(imge2!=null) image1.setImageBitmap(imge2);
            if(imge3!=null) image2.setImageBitmap(imge3);
            if(imge4!=null) image3.setImageBitmap(imge4);
            if(imge0!=null) image4.setImageBitmap(imge0);

        }catch (Exception e){
            e.printStackTrace();
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

    private void deleteDonation() {
        AlertDialog alertDialog = new AlertDialog.Builder(MyDonationsActivity.this).create();
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Are you Sure you want to do that?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteForReal();
                    }
                });
        alertDialog.show();
    }

    private void deleteForReal() {
        loadedDonationItems.remove(selectedDonatedItem);
        refreshRecyclerView();
        closeEditPart();

        new DatabaseManager(mContext,"").deleteDonationItem(selectedDonatedItem);
        selectedDonatedItem = null;
    }

    private void closeEditPart(){
        isEditPartOpen = false;
        editDonationRelativeLayout.animate().translationX(Utils.dpToPx(200)).alpha(0f).scaleY(0.8f).scaleX(0.8f).setDuration(mAnimationTime)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                editDonationRelativeLayout.setVisibility(View.GONE);
                editDonationRelativeLayout.setTranslationX(Utils.dpToPx(200));
                editDonationRelativeLayout.setAlpha(0f);
                editDonationRelativeLayout.setScaleY(0.8f);
                editDonationRelativeLayout.setScaleX(0.8f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void openEditor() {
        isSetNewDataOpen = true;
        editDetailsRelativeLayout.setVisibility(View.VISIBLE);
        blackBack.setVisibility(View.VISIBLE);
        editDetailsRelativeLayout.animate().alpha(1f).translationY(0).setDuration(mAnimationTime).setInterpolator(new LinearOutSlowInInterpolator()).start();
        blackBack.animate().alpha(0.8f).setDuration(mAnimationTime).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                editDetailsRelativeLayout.setAlpha(1f);
                editDetailsRelativeLayout.setTranslationY(0);
                blackBack.setAlpha(0.8f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        nameAndMassEditText.setText(selectedDonatedItem.getItemName());
        itemDetailsEditText.setText(selectedDonatedItem.getItemDetails());

        finishedEditingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameAndMassEditText.getText().toString().trim();
                String newDetails = itemDetailsEditText.getText().toString().trim();

                if(newName.equals("")) nameAndMassEditText.setError("You can't leave this empty.");
                else if(newDetails.equals(""))itemDetailsEditText.setError("You can't leave this empty");
                else{
                    updateDetails(newName, newDetails);
                    closeEditor();
                    Toast.makeText(mContext,"Done!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDetails(String newName, String newDetails) {
        loadedDonationItems.get(loadedDonationItems.indexOf(selectedDonatedItem)).setItemName(newName);
        loadedDonationItems.get(loadedDonationItems.indexOf(selectedDonatedItem)).setItemDetails(newDetails);
        selectedDonatedItem.setItemDetails(newDetails);
        selectedDonatedItem.setItemName(newName);

        new DatabaseManager(mContext,"").updateNameDataInFirebase(selectedDonatedItem,newName);
        new DatabaseManager(mContext,"").updateDetailsDataInFirebase(selectedDonatedItem,newDetails);

        refreshRecyclerView();
    }

    private void closeEditor(){
        isSetNewDataOpen = false;
        editDetailsRelativeLayout.animate().alpha(0f).translationY(Utils.dpToPx(-200)).setDuration(mAnimationTime).setInterpolator(new LinearOutSlowInInterpolator()).start();
        blackBack.animate().alpha(0f).setDuration(mAnimationTime).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                editDetailsRelativeLayout.setAlpha(0f);
                editDetailsRelativeLayout.setTranslationY(Utils.dpToPx(-200));
                blackBack.setAlpha(0f);
                editDetailsRelativeLayout.setVisibility(View.GONE);
                blackBack.setVisibility(View.GONE);
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
        if(isEditPartOpen) closeEditPart();
        else if(isSetNewDataOpen) closeEditor();
        else{
            super.onBackPressed();
        }
    }

}
