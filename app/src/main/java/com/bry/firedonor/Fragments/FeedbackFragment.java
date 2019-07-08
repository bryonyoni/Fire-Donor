package com.bry.firedonor.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bry.firedonor.Constants;
import com.bry.firedonor.R;
import com.bry.firedonor.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FeedbackFragment extends DialogFragment implements View.OnClickListener {
    private Button submitButton;
    private Button cancelButton;
    private EditText editText;
    private Spinner spinner;

    private Context mContext;
    private String mKey = "";


    public void setfragContext(Context context){
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView =  inflater.inflate(R.layout.fragment_feedbck, container, false);
        submitButton = rootView.findViewById(R.id.SubmitAll);
        cancelButton = rootView.findViewById(R.id.cancelAll);
        editText = rootView.findViewById(R.id.feedback);
        spinner = rootView.findViewById(R.id.SpinnerFeedbackType);

        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        return rootView;
    }


    @Override
    public void onClick(View v) {
        if(v == cancelButton){
            dismiss();
        }else if(v == submitButton){
            if(isNetworkConnected(mContext)){
                String feedback = editText.getText().toString();
                String feedbackType = spinner.getSelectedItem().toString();
                if(!feedback.isEmpty()) uploadFeedBackToDatabase(feedback,feedbackType);
                else editText.setError("Say something!");
            }else{
                Toast.makeText(mContext,"You need to be connected to the internet to send your feedback",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFeedBackToDatabase(String feedback, String feedbackType) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        String message = feedbackType+" - "+user+" says : "+feedback;
        DatabaseReference mRef3 = FirebaseDatabase.getInstance().getReference(Constants.FEEDBACK);
        DatabaseReference dbRef = mRef3.push();

        dbRef.child("feedbacktype").setValue(feedbackType);
        dbRef.child("message").setValue(feedback);
        dbRef.child("user").setValue(user);
//        dbRef.setValue(message);
        Toast.makeText(mContext,"Feedback sent.",Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private boolean isNetworkConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    @Override
    public void dismiss(){
        super.dismiss();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }


}
