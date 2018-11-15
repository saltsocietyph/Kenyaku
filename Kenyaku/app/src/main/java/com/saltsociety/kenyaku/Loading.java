package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Loading extends AppCompatActivity {

    // Values
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static int SPLASH_TIME = 5000;
    private boolean isUserNull = true;

    // Views
    TextView loadingText;

    // Classes
    GetCurrentUser getCurrentUser;

    @Override
    public void onStart() {
        super.onStart();

        // Get firebase authentication object
        mAuth = FirebaseAuth.getInstance();
        // Get currently signed-in user
        currentUser = mAuth.getCurrentUser();
        isUserNull();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        customFont();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isUserNull) {
                    Log.d("DEBUG", "User is not null.");
                    // Means the user is still signed-in
                    if(currentUser.isEmailVerified()) {
                        // Check if user is using app for the first time
                        DocumentReference userInfo = FirebaseFirestore.getInstance()
                                .collection("users").document(currentUser.getUid());

                        userInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()) {
                                        Map<String, Object> user = document.getData();
                                        if((boolean)user.get("firstLaunch")) {
                                            // Redirect to Setup User info
                                            Intent setupInfoScr = new Intent(getApplicationContext(), SetUpUserInfo.class);
                                            startActivity(setupInfoScr);
                                            finish();
                                        } else {
                                            // Redirect to Home Screen
                                            Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
                                            startActivity(homeScr);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error occurred. Document doesn't exist.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // Redirect to Login Screen
                        Intent loginScr = new Intent(getApplicationContext(), LoginScreen.class);
                        startActivity(loginScr);
                        finish();
                    }

                } else {
                    // Redirect to Login Screen
                    Intent loginScr = new Intent(getApplicationContext(), LoginScreen.class);
                    startActivity(loginScr);
                    finish();

                    // Or redirect to Tutorial
                }
            }
        }, SPLASH_TIME);
    }

    private void customFont() {
        Typeface heroF = Typeface.createFromAsset(getAssets(), "fonts/Hero.otf");

        // Get text view
        loadingText = findViewById(R.id.loadingText);
        // Set font
        loadingText.setTypeface(heroF);
    }

    private void isUserNull() {
        getCurrentUser = new GetCurrentUser(currentUser);
        isUserNull = getCurrentUser.isUserNull();
    }

}
