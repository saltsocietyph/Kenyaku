package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String email, username, password, userId;
    private boolean isTaskSuccessful;

    // Views
    TextView subtitle;
    EditText emailText, usernameText, pwText, cpwText;
    Button submitBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeAuth();
        fetchViews();
        // customFont();
        clearFocus();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void fetchViews() {
        subtitle = findViewById(R.id.subtitle);

        emailText = findViewById(R.id.emailText);
        usernameText = findViewById(R.id.usernameText);
        pwText = findViewById(R.id.passwordText);
        cpwText = findViewById(R.id.confirmPassword);

        submitBtn = findViewById(R.id.submitBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
    }

    private void customFont() {
        Typeface heroF = Typeface.createFromAsset(getAssets(), "fonts/Hero.otf");

        subtitle.setTypeface(heroF);
        emailText.setTypeface(heroF);
        usernameText.setTypeface(heroF);
        pwText.setTypeface(heroF);
        cpwText.setTypeface(heroF);
        submitBtn.setTypeface(heroF);
        cancelBtn.setTypeface(heroF);
    }

    private void clearFocus() {
        emailText.clearFocus();
        usernameText.clearFocus();
        pwText.clearFocus();
        cpwText.clearFocus();
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Check if creation of account is successful
                        if(task.isSuccessful()) {
                            isTaskSuccessful = true;
                            Toast.makeText(getApplicationContext(), "User successfully created.",
                                    Toast.LENGTH_LONG).show();

                            currentUser = mAuth.getCurrentUser();
                            userId = currentUser.getUid();
                            saveUser();
                        } else {
                            isTaskSuccessful = false;
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            Log.w("EXCEPTION", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    private void saveUser() {
        DocumentReference docRef = FirebaseFirestore.getInstance().document("users/" + userId);

        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("userId", userId);
        userInfo.put("email", email);
        userInfo.put("username", username);
        userInfo.put("password", password);
        userInfo.put("firstLaunch", true);

        docRef.set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    // Redirect to Confirm Email Page
                    Intent confirmEmail = new Intent (getApplicationContext(), ConfirmEmail.class);
                    startActivity(confirmEmail);
                    finish();
                } else {
                    // Show error
                    Log.w("EXCEPTION", "saveUserToDatabase:failure", task.getException());
                }
            }
        });
    }

    // Button Functions
    public void submit(View v) {
        // Check if textfields are not empty
        if(emailText.getText().toString().isEmpty() || usernameText.getText().toString().isEmpty() ||
                pwText.getText().toString().isEmpty() || cpwText.getText().toString().isEmpty()) {
            // Show error
            Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_LONG).show();
        } else {
            // Check if password entered is the same
            if(pwText.getText().toString().equals(cpwText.getText().toString())) {
                email = emailText.getText().toString();
                username = usernameText.getText().toString();
                password = pwText.getText().toString();

                createUser();
            } else {
                // Show error
                Toast.makeText(getApplicationContext(), "Password didn't match.", Toast.LENGTH_LONG).show();

                // Clear fields for password
                pwText.setText(null);
                cpwText.setText(null);
            }
        }
    }

    public void cancel(View v) {
        Intent loginScr = new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(loginScr);
        finish();
    }


}
