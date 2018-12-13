package com.saltsociety.kenyaku;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
    private static final String TAG = "Registration";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String email, username, password, userId, phoneNumber;
    private boolean isTaskSuccessful;

    private BroadcastReceiver smsSentBr, smsDeliverBr;
    private PendingIntent sentPi, deliveredPi;

    // Views
    TextView subtitle;
    EditText emailText, usernameText, pwText, cpwText, phoneNumberText;
    Button submitBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeAuth();
        fetchViews();
        clearFocus();

        setUpIntentAndReceivers();
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
        phoneNumberText = findViewById(R.id.phoneNumberText);

        submitBtn = findViewById(R.id.submitBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
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
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
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
        userInfo.put("phoneNumber", phoneNumber);
        userInfo.put("firstLaunch", true);

        docRef.set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    // Redirect to Confirm Email Page
                    String message = "Account created!";
                    sendSms(message, phoneNumber);

                    Intent confirmEmail = new Intent (getApplicationContext(), ConfirmEmail.class);
                    startActivity(confirmEmail);
                    finish();
                } else {
                    // Show error
                    Log.w(TAG, "saveUserToDatabase:failure", task.getException());
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
                phoneNumber = phoneNumberText.getText().toString();

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
    private void setUpIntentAndReceivers() {
        sentPi = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        deliveredPi = PendingIntent.getBroadcast(this, 0,
                new Intent("SMS_SENT"), 0);
    }

    public void sendSms(String message, String number) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    1);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, sentPi, deliveredPi);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message sent!",
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure.",
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service.",
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off.",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        smsDeliverBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message delivered!",
                                Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "Message not delivered!",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        registerReceiver(smsSentBr, new IntentFilter("SMS_SENT"));
        registerReceiver(smsDeliverBr, new IntentFilter("SMS_DELIVERED"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsDeliverBr);
        unregisterReceiver(smsSentBr);
    }
}
