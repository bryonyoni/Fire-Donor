package com.bry.firedonor.Activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bry.firedonor.R;
import com.bry.firedonor.Services.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = SignupActivity.class.getSimpleName();
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Bind(R.id.emailLinearLayout) LinearLayout emailLinearLayout;
    @Bind(R.id.emailEditText) EditText emailEditText;
    private boolean isShowingEmailView = false;
    private String mEnteredEmailString = "";

    @Bind(R.id.passwordLinearLayout) LinearLayout passwordLinearLayout;
    @Bind(R.id.passwordEditText) EditText passwordEditText;
    private boolean isShowingPasswordView = false;
    private String mEnteredPasswordString = "";

    @Bind(R.id.previousButton) ImageButton previousButton;
    @Bind(R.id.nextButton) Button nextButton;
    @Bind(R.id.signUpLink) TextView signUpLink;
    @Bind(R.id.progressBarRelativeLayout) RelativeLayout progressBarRelativeLayout;
    @Bind(R.id.loginLinearLayout) LinearLayout loginLinearLayout;
    private boolean isShowingSpinner = false;

    private int mAnimationDuration = 300;
    private int transitionOutTranslation = -200;
    private int transitionInTranslation = 400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);//Binds all the views into their respective targets.

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"User was found, opening main activity");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mContext = this.getApplicationContext();
        setUpEmailInputView();

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShowingSpinner) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void setUpEmailInputView() {
        isShowingEmailView = true;
        previousButton.setVisibility(View.GONE);
        if(!mEnteredEmailString.equals("")) emailEditText.setText(mEnteredEmailString);
        emailLinearLayout.setVisibility(View.VISIBLE);
        emailLinearLayout.animate().translationX(0).alpha(1f).setInterpolator(new LinearOutSlowInInterpolator())
                .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                emailLinearLayout.setTranslationX(0);
                emailLinearLayout.setAlpha(1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        nextButton.setText(getResources().getString(R.string.next));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnteredEmailString = emailEditText.getText().toString().trim();
                if(mEnteredEmailString.equals("")) emailEditText.setError("You need to type your email");
                else{
//                    emailEditText.setText("");
                    isShowingEmailView = false;
                    emailLinearLayout.animate().translationX(Utils.dpToPx(transitionOutTranslation)).alpha(0f).setInterpolator(new LinearOutSlowInInterpolator())
                            .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            emailLinearLayout.setTranslationX(Utils.dpToPx(transitionOutTranslation));
                            emailLinearLayout.setAlpha(0f);
                            emailLinearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();

                    setUpPasswordInputView();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setUpPasswordInputView() {
        isShowingPasswordView = true;
        previousButton.setVisibility(View.VISIBLE);
        if(!mEnteredPasswordString.equals("")) passwordEditText.setText(mEnteredPasswordString);
        passwordLinearLayout.setVisibility(View.VISIBLE);
        passwordLinearLayout.animate().translationX(0).alpha(1f).setInterpolator(new LinearOutSlowInInterpolator())
                .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                passwordLinearLayout.setTranslationX(0);
                passwordLinearLayout.setAlpha(1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        nextButton.setText(getResources().getString(R.string.log_in));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnteredPasswordString = passwordEditText.getText().toString().trim();
                if(mEnteredPasswordString.equals("")) passwordEditText.setError("We need a password.");
                else{
//                    passwordEditText.setText("");
                    isShowingPasswordView = false;
                    loginUser();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowingPasswordView = false;
                passwordLinearLayout.animate().translationX(Utils.dpToPx(transitionInTranslation)).alpha(0f).setInterpolator(new LinearOutSlowInInterpolator())
                        .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        passwordLinearLayout.setTranslationX(Utils.dpToPx(transitionInTranslation));
                        passwordLinearLayout.setAlpha(0f);
                        passwordLinearLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
                setUpEmailInputView();
            }
        });
    }

    private void loginUser(){
        if(!isOnline(mContext)){
            Snackbar.make(findViewById(R.id.activity_login_coordinatorLayout), R.string.no_internet_connection,
                    Snackbar.LENGTH_LONG).show();
        }else{
            showLoadingScreen();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mAuth.signInWithEmailAndPassword(mEnteredEmailString, mEnteredPasswordString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG,"signInWithEmail:onComplete"+task.isSuccessful());
                            if(!task.isSuccessful()){
                                Log.w(TAG,"SignInWithEmail",task.getException());
                                Snackbar.make(findViewById(R.id.activity_signup_coordinatorLayout), getResources().getString(R.string.failed_sign_in), Snackbar.LENGTH_LONG).show();
                                hideLoadingScreens();
                            }
                        }
                    });
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    @Override
    public void onBackPressed(){
        if(!isShowingSpinner) {
            if (isShowingEmailView) {
                super.onBackPressed();
            } else previousButton.performClick();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }

    private void showLoadingScreen(){
        isShowingSpinner = true;
        progressBarRelativeLayout.setVisibility(View.VISIBLE);
        loginLinearLayout.setAlpha(0.3f);
    }

    private void hideLoadingScreens(){
        isShowingSpinner = false;
        progressBarRelativeLayout.setVisibility(View.GONE);
        loginLinearLayout.setAlpha(1f);
    }

}
