package com.bry.firedonor.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bry.firedonor.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyDonationsActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    @Bind(R.id.backButton) ImageView backButton;
    @Bind(R.id.mainLinearLayout) LinearLayout mainLinearLayout;
    @Bind(R.id.progressbarRelativeLayout) RelativeLayout progressbarRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);
        ButterKnife.bind(this);
        mContext = getApplicationContext();

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(backButton)){
            finish();
        }
    }
}
