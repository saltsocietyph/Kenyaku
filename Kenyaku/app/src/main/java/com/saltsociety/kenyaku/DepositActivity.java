package com.saltsociety.kenyaku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DepositActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DocumentReference userRef;

    // Views
    TextView goalNameText, targetMoneyText, daysToFinishText;
    EditText amountToDepositText, dateTodayText;
    ImageView goalTypeIcon;

    String currentGoalId, goalName, goalType;
    double targetMoney;
    long daysToFinish, achievedGoals;
    DecimalFormat decimalFormat;

    double requiredPerDay, currentSavings;
    boolean isGoalFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        fetchViews();
        clearFocus();

        initializeAuth();
        getUser();
        getCurrentGoalId();
        setDate();
    }

    private void fetchViews() {
        goalNameText = findViewById(R.id.goalNameText);
        targetMoneyText = findViewById(R.id.targetMoneyText);
        daysToFinishText = findViewById(R.id.daysToFinishText);
        amountToDepositText = findViewById(R.id.amounToDepostText);
        dateTodayText = findViewById(R.id.dateTodayText);
        goalTypeIcon = findViewById(R.id.goalTypeIcon);
    }

    private void clearFocus() {
        amountToDepositText.clearFocus();
        dateTodayText.clearFocus();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void getCurrentGoalId() {
        DocumentReference userDocument = FirebaseFirestore.getInstance().
                collection("users").document(currentUser.getUid());

        Log.d("DEBUG", currentUser.getUid());
        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Map<String, Object> userInfo = document.getData();
                        currentGoalId = userInfo.get("currentGoal").toString();
                        getCurrentGoalDetails();
                    } else {
                        Toast.makeText(getApplicationContext(), "1: Error occurred. Document doesn't exist.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCurrentGoalDetails() {
        Log.d("DEBUG", currentGoalId);
        DocumentReference goalDoc = FirebaseFirestore.getInstance().
                collection("goals").document(currentGoalId);

        goalDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Map<String, Object> goalData = document.getData();

                        decimalFormat = new DecimalFormat(".00");
                        goalName = goalData.get("goalName").toString();
                        goalType = goalData.get("goalType").toString();
                        targetMoney = (double) goalData.get("leftToSave");
                        daysToFinish = (long) goalData.get("remainingDays");
                        requiredPerDay = (double) goalData.get("requiredPerDay");
                        currentSavings = (double) goalData.get("currentSavings");


                        goalNameText.setText(goalName);
                        targetMoneyText.setText("" + targetMoney);
                        daysToFinishText.setText(daysToFinish + " Days");
                        setGoalTypeIcon();
                    } else {
                        Toast.makeText(getApplicationContext(), "2: Error occurred. Document doesn't exist.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        });
    }

    private void setDate() {
        String dateFormatStr = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr, Locale.JAPAN);
        dateTodayText.setText(dateFormat.format(new Date()));
    }

    private void setGoalTypeIcon() {
        if(goalType.equals("Just Cash Goal")) {
            goalTypeIcon.setImageResource(R.drawable.money);
        } else if(goalType.equals("Material Goal")) {
            goalTypeIcon.setImageResource(R.drawable.ps4);
        } else if(goalType.equals("Event Goal")) {
            goalTypeIcon.setImageResource(R.drawable.spotlights);
        }
    }

    public void cancel(View v) {
        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }

    public void deposit(View v) {
        if(amountToDepositText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter amount to deposit.", Toast.LENGTH_LONG).show();
        } else {
            // Check if amount to deposit is greater than required per day
            double amountToDeposit = Double.parseDouble(amountToDepositText.getText().toString());
            Log.d("DEBUG", "" + amountToDeposit + " > " + requiredPerDay);

            DepositComputation depComp = new DepositComputation();
            if(depComp.checkIfDepositIsEnough(requiredPerDay, amountToDeposit)) {
                DocumentReference goalData = FirebaseFirestore.getInstance().
                        collection("goals").document(currentGoalId);

                goalData.update("currentSavings", currentSavings + amountToDeposit);
                goalData.update("remainingDays", daysToFinish - depComp.daysToFinish(amountToDeposit, requiredPerDay));
                goalData.update("leftToSave", targetMoney - amountToDeposit);

                requiredPerDay = depComp.recalculateRequiredPerDay(targetMoney - amountToDeposit,
                        (int) daysToFinish - depComp.daysToFinish(amountToDeposit, requiredPerDay));
                goalData.update("requiredPerDay", requiredPerDay);
                isGoalFinished = depComp.isGoalFinished(targetMoney, currentSavings + amountToDeposit);
                goalData.update("isFinished", isGoalFinished);
                goalData.update("endDate", new Date());

                Log.d("DEBUG", ""+ isGoalFinished);
                if(isGoalFinished) {
                    userRef = FirebaseFirestore.getInstance()
                            .collection("users").document(currentUser.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if(snapshot.exists()) {
                                    Map<String, Object> userInfo = snapshot.getData();

                                    achievedGoals = (long) userInfo.get("achievedGoals");
                                    userRef.update("achievedGoals", achievedGoals + 1);
                                } else {
                                    Toast.makeText(getApplicationContext(), "An error has occurred.",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    userRef.update("currentGoal", "");

                }

                DocumentReference depositRef = FirebaseFirestore.getInstance()
                        .collection("deposits").document();

                Map<String, Object> depositData = new HashMap<>();
                depositData.put("userId", currentUser.getUid());
                depositData.put("goalId", currentGoalId);
                depositData.put("goalName", goalName);
                depositData.put("date", new Date());
                depositData.put("amount", amountToDeposit);

                depositRef.set(depositData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Deposit recorded.",
                                    Toast.LENGTH_LONG).show();

                            Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
                            startActivity(homeScr);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Amount to deposit should be greater than the required money to save per day.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
