package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Views
    Button loginBtn, createBtn;
    TextView emailLabel, pwLabel, developers, yearDeveloped, subtitle;
    EditText emailText, pwText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        initializeAuth();
        getUser();

        fetchViews();
        // customFont();
        clearFocus();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void fetchViews() {
        loginBtn = findViewById(R.id.loginBtn);
        createBtn = findViewById(R.id.createAccBtn);

        emailLabel = findViewById(R.id.emailLabel);
        pwLabel = findViewById(R.id.passwordLabel);
        developers = findViewById(R.id.developers);
        yearDeveloped = findViewById(R.id.yearDeveloped);
        subtitle = findViewById(R.id.subtitle);

        emailText = findViewById(R.id.emailText);
        pwText = findViewById(R.id.passwordText);
    }

    private void customFont() {
        // Create a Typeface for a Custom Font from Assets folder
        Typeface heavyDock11F = Typeface.createFromAsset(getAssets(), "fonts/heavy_dock11.otf");
        Typeface heroF = Typeface.createFromAsset(getAssets(), "fonts/Hero.otf");

        // Set Custom Font
        loginBtn.setTypeface(heroF);
        createBtn.setTypeface(heroF);
        /* emailLabel.setTypeface(heroF);
        pwLabel.setTypeface(heroF); */
        developers.setTypeface(heroF);
        yearDeveloped.setTypeface(heroF);
        /* emailText.setTypeface(heroF);
        pwText.setTypeface(heroF); */
        subtitle.setTypeface(heroF);
    }

    private void clearFocus() {
        emailText.clearFocus();
        pwText.clearFocus();
    }

    // Button Functions
    public void login(View v) {
        // Check if text fields are empty
        if(emailText.getText().toString().isEmpty() || pwText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter your email and password to log in.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Sign in
            String email = emailText.getText().toString();
            String password = pwText.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                // Avoid null exception by getting firebase user after log in
                                if(currentUser == null) {
                                    currentUser = mAuth.getCurrentUser();
                                }

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
                                    Toast.makeText(getApplicationContext(), "Email is not yet verified.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "" +
                                    task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void createAccount(View v) {
        Intent registrationScr = new Intent(getApplicationContext(), Registration.class);
        startActivity(registrationScr);
        finish();
    }
}
