package com.saltsociety.kenyaku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SetUpUserInfo extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    // Views
    EditText allowanceText, daysText, otherDaysText, foodExpText, transpoExpText, othersExpText;
    Button nextBtn;

    // Data
    CheckAllowance allowanceChecker;
    double allowancePerDay, foodExp, transpoExp, othersExp, savePerDay;
    int daysWithAllowance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_user_info);

        fetchViews();
        clearFocus();
        initializeAuth();
        getUser();
    }

    private void fetchViews() {
        allowanceText = findViewById(R.id.allowanceText);
        daysText = findViewById(R.id.daysWithAllowanceText);
        otherDaysText = findViewById(R.id.otherDaysText);
        foodExpText = findViewById(R.id.foodExpenseText);
        transpoExpText = findViewById(R.id.transpoExpoText);
        othersExpText = findViewById(R.id.otherExpenseText);
        nextBtn = findViewById(R.id.nextBtn);

        otherDaysText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                daysText.setText(otherDaysText.getText().toString());
            }
        });
    }

    private void clearFocus() {
        allowanceText.clearFocus();
        daysText.clearFocus();
        otherDaysText.clearFocus();
        foodExpText.clearFocus();
        transpoExpText.clearFocus();
        othersExpText.clearFocus();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private boolean computeAllowance() {
        allowanceChecker = new CheckAllowance(allowancePerDay, daysWithAllowance, foodExp, transpoExp, othersExp);

        return allowanceChecker.checkAllowance();
    }

    private void addUserAllowanceInfo() {
        DocumentReference userDocument = FirebaseFirestore.getInstance().document("users/" + currentUser.getUid());

        userDocument.update("firstLaunch", false);

        Map<String, Object> allowanceInfo = new HashMap<>();
        allowanceInfo.put("allowancePerDay", allowancePerDay);
        allowanceInfo.put("daysWithAllowance", daysWithAllowance);
        allowanceInfo.put("foodExpenses", foodExp);
        allowanceInfo.put("transpoExpenses", transpoExp);
        allowanceInfo.put("othersExpenses", othersExp);
        allowanceInfo.put("savePerDay", savePerDay);
        allowanceInfo.put("currentGoal", "");
        allowanceInfo.put("totalGoals", 0);
        allowanceInfo.put("achievedGoals", 0);

        userDocument.set(allowanceInfo, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Successfully added your allowance info!", Toast.LENGTH_LONG).show();

                    // Redirect to Home Screen
                    Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(homeScr);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void fiveDays(View v) {
        daysText.setText("5");
    }

    public void sevenDays(View v) {
        daysText.setText("7");
    }

    public void next(View v) {
        // Check if text fields are not empty
        if(allowanceText.getText().toString().isEmpty() || daysText.getText().toString().isEmpty()|| foodExpText.getText().toString().isEmpty()
                || transpoExpText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_LONG).show();
        } else {
            // Get Values
            allowancePerDay = Double.parseDouble(allowanceText.getText().toString());
            daysWithAllowance = Integer.parseInt(daysText.getText().toString());
            foodExp = Double.parseDouble(foodExpText.getText().toString());
            transpoExp = Double.parseDouble(transpoExpText.getText().toString());

            // Check if other expenses have value
            othersExp = 0.00;
            if(!othersExpText.getText().toString().isEmpty())
                othersExp = Double.parseDouble(othersExpText.getText().toString());

            // Check if expenses is greater than allowance
            if(computeAllowance()) {
                savePerDay = allowanceChecker.savePerDay();
                addUserAllowanceInfo();
            } else {
                Toast.makeText(getApplicationContext(), "Your expenses is greater than your allowance.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
