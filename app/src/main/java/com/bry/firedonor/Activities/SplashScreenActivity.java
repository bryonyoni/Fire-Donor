package com.bry.firedonor.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bry.firedonor.R;
import com.bry.firedonor.Services.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class SplashScreenActivity extends AppCompatActivity {
    private int SPLASH_DISPLAY_LENGTH = 3021;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        hideNavBars();
        final TextView beginText = findViewById(R.id.beginTextView);
        final ImageView logoImage = findViewById(R.id.logoImage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logoImage.animate().translationY(0).setDuration(300).setInterpolator(new LinearOutSlowInInterpolator()).start();
                beginText.animate().alpha(1f).setDuration(300).setInterpolator(new LinearOutSlowInInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        beginText.setAlpha(1f);
                        logoImage.setTranslationY(0);
                        beginText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        },SPLASH_DISPLAY_LENGTH);

        beginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = "";
                try{
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent;
                if(!uid.equals("")){
                    intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                }else {
                    if (new SharedPreferenceManager(getApplicationContext()).isFirstTimeLaunch()) {
                        intent = new Intent(SplashScreenActivity.this, SignupActivity.class);
                    } else {
                        intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    }
                }
                startActivity(intent);
                finish();
            }
        });
    }


    private void hideNavBars() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
