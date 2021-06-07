package com.lousssouarn.edouard.go4lunch.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.api.UserFirebase;
import com.lousssouarn.edouard.go4lunch.model.Restaurant;

import java.util.Collections;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    //For UI
    Button googleButton;
    Button facebookButton;

    //For data
    private final static int RC_SIGN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        googleButton = findViewById(R.id.auth_activity_google_button);
        facebookButton = findViewById(R.id.auth_activity_facebook_button);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInWithGoogle();

            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInWithFacebook();

            }
        });
    }

    /**
     * Sign in With Google
     */
    private void startSignInWithGoogle() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN);
    }

    /**
     * Sign in With Facebook
     */
    private void startSignInWithFacebook() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN);
    }

    /**
     * Use it for the Response of SignIn
     */
    private void responseSignIn(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RC_SIGN)
        {
            if (resultCode == RESULT_OK)
            {
                createUserInFireStore();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.response_sign_in_success),Toast.LENGTH_SHORT ).show();
                startMainActivity();
            }
            else
            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.response_sign_in_error),Toast.LENGTH_SHORT ).show();
            }
        }
    }

    /**
     * Use it for recover the current user
     */
    @Nullable
    protected FirebaseUser getCurrentUser(){ return  FirebaseAuth.getInstance().getCurrentUser(); }

    /**
     * Use it for add the new user in FireStore
     */
    private void createUserInFireStore(){

       if(getCurrentUser() != null){

           String uId  =FirebaseAuth.getInstance().getCurrentUser().getUid();
           String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
           String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
           String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
           String goRestaurant = "";
           List<Restaurant> favorites = null;

           UserFirebase.createUser(uId, name, email, urlPicture, goRestaurant, favorites ).addOnFailureListener(this.onFailureListener());
       }
    }

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.responseSignIn(requestCode,resultCode, data);
    }
    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {}
}