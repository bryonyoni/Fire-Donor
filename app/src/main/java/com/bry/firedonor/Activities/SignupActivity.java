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
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bry.firedonor.R;
import com.bry.firedonor.Services.DatabaseManager;
import com.bry.firedonor.Services.SharedPreferenceManager;
import com.bry.firedonor.Services.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private final String TAG = SignupActivity.class.getSimpleName();
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Bind(R.id.nameLinearLayout) LinearLayout nameLinearLayout;
    @Bind(R.id.nameEditText) EditText nameEditText;
    private boolean isShowingNameView = true;
    private String mEnteredNameString = "";

    @Bind(R.id.emailLinearLayout) LinearLayout emailLinearLayout;
    @Bind(R.id.emailEditText) EditText emailEditText;
    private boolean isShowingEmailView = false;
    private String mEnteredEmailString = "";

    @Bind(R.id.passwordLinearLayout) LinearLayout passwordLinearLayout;
    @Bind(R.id.passwordEditText) EditText passwordEditText;
    private boolean isShowingPasswordView = false;
    private String mEnteredPasswordString = "";

    @Bind(R.id.retypePasswordLinearLayout) LinearLayout retypePasswordLinearLayout;
    @Bind(R.id.passwordRetypeEditText) EditText passwordRetypeEditText;
    private boolean isShowingRetypePasswordView = true;
    private String mEnteredRetypedPasswordString = "";

    @Bind(R.id.previousButton) ImageButton previousButton;
    @Bind(R.id.nextButton) Button nextButton;
    @Bind(R.id.signInLink) TextView signInLink;
    @Bind(R.id.progressBarRelativeLayout) RelativeLayout progressBarRelativeLayout;
    @Bind(R.id.signUpLinearLayout) LinearLayout signUpLinearLayout;
    private boolean isShowingSpinner = false;

    private List<String> easyPasswords = new ArrayList<>(Arrays.asList
            ("123456", "987654","qwerty","asdfgh","zxcvbn","123456abc","123456qwe","987654qwe", "987654asd",""));
    private int mAnimationDuration = 300;
    private int transitionOutTranslation = -200;
    private int transitionInTranslation = 400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);//Binds all the views into their respective targets.

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"User was just created, opening main activity");
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mContext = this.getApplicationContext();
        setUpNameInputView();

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }



    private void setUpNameInputView() {
        isShowingNameView = true;
        previousButton.setVisibility(View.GONE);
        nameLinearLayout.setVisibility(View.VISIBLE);
        if(!mEnteredNameString.equals("")) nameEditText.setText(mEnteredNameString);
        nameLinearLayout.animate().translationX(0).alpha(1f).setInterpolator(new LinearOutSlowInInterpolator())
                .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                nameLinearLayout.setVisibility(View.VISIBLE);
                nameLinearLayout.setTranslationX(0);
                nameLinearLayout.setAlpha(1f);
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
                mEnteredNameString = nameEditText.getText().toString().trim();
                if(mEnteredNameString.equals("")) nameEditText.setError("Please enter a name");
                else if(mEnteredNameString.length()>16) nameEditText.setError("That name is too long!");
                else{
//                    nameEditText.setText("");
                    isShowingNameView = false;
                    nameLinearLayout.animate().translationX(Utils.dpToPx(transitionOutTranslation)).alpha(0f).setInterpolator(new LinearOutSlowInInterpolator())
                            .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            nameLinearLayout.setVisibility(View.GONE);
                            nameLinearLayout.setTranslationX(Utils.dpToPx(transitionOutTranslation));
                            nameLinearLayout.setAlpha(0f);
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
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setUpEmailInputView() {
        isShowingEmailView = true;
        previousButton.setVisibility(View.VISIBLE);
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
                if(isValidEmail(mEnteredEmailString)){
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
                isShowingEmailView = false;
                emailLinearLayout.animate().translationX(Utils.dpToPx(transitionInTranslation)).alpha(0f).setInterpolator(new LinearOutSlowInInterpolator())
                        .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        emailLinearLayout.setTranslationX(Utils.dpToPx(transitionInTranslation));
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
                setUpNameInputView();
            }
        });
    }

    private boolean isValidEmail(String email) {
        if(email.equals("")){
            emailEditText.setError("We need your email.");
            return false;
        }

        boolean isGoodEmail = (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());

        if(!email.contains("@")){
            emailEditText.setError("That's not an email address.");
            return false;
        }

        int counter = 0;
        for( int i=0; i<email.length(); i++ ) {
            if(email.charAt(i) == '.' ) {
                counter++;
            }
        }
        if(counter!=1 && counter!=2 && counter!=3){
            emailEditText.setError("We need your actual email address.");
            return false;
        }

        int counter2 = 0;
        boolean continueIncrement = true;
        for( int i=0; i<email.length(); i++ ) {
            if(email.charAt(i) == '@' ) {
                continueIncrement = false;
            }
            if(continueIncrement)counter2++;
        }
        if(counter2<=3){
            emailEditText.setError("That's not a real email address");
            return false;
        }

        if(!isGoodEmail){
            emailEditText.setError("We need your actual email address please");
            return false;
        }
        return isGoodEmail;
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

        nextButton.setText(getResources().getString(R.string.next));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnteredPasswordString = passwordEditText.getText().toString().trim();
                if(mEnteredPasswordString.equals("")) passwordEditText.setError("We need a password.");
                else if(mEnteredPasswordString.length() < 6) passwordEditText.setError("Your password needs to be at least 6 characters");
                else if(easyPasswords.contains(mEnteredPasswordString)) passwordEditText.setError("You can't use that password.");
                else{
//                    passwordEditText.setText("");
                    isShowingPasswordView = false;
                    passwordLinearLayout.animate().translationX(Utils.dpToPx(transitionOutTranslation)).alpha(0f).setInterpolator(new LinearOutSlowInInterpolator())
                            .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            passwordLinearLayout.setTranslationX(Utils.dpToPx(transitionOutTranslation));
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

                    setUpConfirmPasswordView();
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

    private void setUpConfirmPasswordView() {
        isShowingRetypePasswordView = true;
        previousButton.setVisibility(View.VISIBLE);
        if(!mEnteredRetypedPasswordString.equals("")) passwordRetypeEditText.setText(mEnteredRetypedPasswordString);
        retypePasswordLinearLayout.setVisibility(View.VISIBLE);
        retypePasswordLinearLayout.animate().translationX(0).alpha(1f).setInterpolator(new LinearOutSlowInInterpolator())
                .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                retypePasswordLinearLayout.setTranslationX(0);
                retypePasswordLinearLayout.setAlpha(1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        nextButton.setText(getResources().getString(R.string.finish));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnteredRetypedPasswordString = passwordRetypeEditText.getText().toString().trim();
                if(mEnteredRetypedPasswordString.equals("")) passwordRetypeEditText.setError("Please retype the password.");
                else if(!mEnteredRetypedPasswordString.equals(mEnteredPasswordString)) passwordRetypeEditText.setError("The passwords don't match.");
                else{
//                    passwordRetypeEditText.setText("");
//                    isShowingRetypePasswordView = false;
                    setUpNewUser();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowingRetypePasswordView = false;
                retypePasswordLinearLayout.animate().translationX(Utils.dpToPx(transitionInTranslation)).alpha(0f).setInterpolator(new LinearOutSlowInInterpolator())
                        .setDuration(mAnimationDuration).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        retypePasswordLinearLayout.setTranslationX(Utils.dpToPx(transitionInTranslation));
                        retypePasswordLinearLayout.setAlpha(0f);
                        retypePasswordLinearLayout.setVisibility(View.GONE);
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
        });
    }

    @Override
    public void onBackPressed(){
        if(!isShowingSpinner) {
            if (isShowingNameView) {
                super.onBackPressed();
            } else previousButton.performClick();
        }
    }

    private void setUpNewUser() {
        if(!isOnline(mContext)){
            Snackbar.make(findViewById(R.id.activity_signup_coordinatorLayout), R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
        } else{
            previousButton.setClickable(false);
            nextButton.setClickable(false);
            showLoadingScreen();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mAuth.createUserWithEmailAndPassword(mEnteredEmailString,mEnteredPasswordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG,"authentication successful");
                        createFirebaseUserProfile(task.getResult().getUser());
                    }else {
                        Snackbar.make(findViewById(R.id.activity_signup_coordinatorLayout), getResources().getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show();
                        previousButton.setClickable(true);
                        nextButton.setClickable(true);
                        hideLoadingScreens();
                    }
                }
            });
        }

    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder().setDisplayName(mEnteredNameString).build();
        user.updateProfile(addProfileName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Created new username,");
                    new SharedPreferenceManager(mContext).setNameInSharedPref(mEnteredNameString).setEmailInSharedPref(mEnteredEmailString);
                    new DatabaseManager(mContext,"").setUpNewUserInFirebase(mEnteredNameString,mEnteredEmailString);
                }
            }
        });
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
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
        signUpLinearLayout.setAlpha(0.3f);
    }

    private void hideLoadingScreens(){
        isShowingSpinner = false;
        progressBarRelativeLayout.setVisibility(View.GONE);
        signUpLinearLayout.setAlpha(1f);
    }


}
