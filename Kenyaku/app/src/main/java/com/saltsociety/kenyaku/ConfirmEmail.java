package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean emailVerificationSuccessful;

    // Views
    TextView messageText;
    Button verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);

        initializeAuth();
        getUser();

        fetchViews();
        // customFont();

        sendEmailVerification();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        user = mAuth.getCurrentUser();
    }

    private void fetchViews() {
        messageText = findViewById(R.id.messageText);
        verifyBtn = findViewById(R.id.verifyBtn);
    }

    private void customFont() {
        Typeface heroF = Typeface.createFromAsset(getAssets(), "fonts/Hero.otf");

        messageText.setTypeface(heroF);
        verifyBtn.setTypeface(heroF);
    }

    private void sendEmailVerification() {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            // Show status of activity
                            Toast.makeText(getApplicationContext(), "Verification email sent to " +
                                    user.getEmail(), Toast.LENGTH_LONG).show();

                            emailVerificationSuccessful = true;
                            verifyBtn.setText("Next");
                            verifyBtn.setEnabled(true);

                        } else {
                            // Show status of activity
                            Log.e("EXCEPTION", "sendEmailVerification", task.getException());
                            Toast.makeText(getApplicationContext(), "Failed to send verification email.",
                                    Toast.LENGTH_LONG).show();

                            // Change button name and function to try again
                            emailVerificationSuccessful = false;
                            verifyBtn.setText("Try Again");
                            verifyBtn.setEnabled(true);
                        }
                    }
                });
    }

    public void verifyEmail(View v) {
        // If sending of email verification is successful, redirect to Log In Screen
        if (emailVerificationSuccessful) {
            // Sign out user
            signOut();

            // Redirect to Log In Screen
            Intent loginScr = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(loginScr);
            finish();
        } else {
            sendEmailVerification();
            verifyBtn.setEnabled(false);
        }
    }

    private void signOut() {
        mAuth.signOut();
    }
}
