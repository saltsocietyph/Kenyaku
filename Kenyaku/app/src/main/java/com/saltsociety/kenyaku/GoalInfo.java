package com.saltsociety.kenyaku;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Timer;

public class GoalInfo extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DocumentReference userRef;

    TextView goalName, targetMoney, currentSavings, reqPerDay, daysToFinish;
    ImageView goalTypeIcon, doneIcon;

    boolean isCurrentGoal = false, deleteFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_info);

        initializeAuth();
        getUser();

        fetchViews();
        getGoalDetails();
        checkIfCurrentGoal();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void fetchViews() {
        goalName = findViewById(R.id.goalNameLabel);
        targetMoney = findViewById(R.id.targetMoneyText);
        currentSavings = findViewById(R.id.currentSavingsText);
        reqPerDay = findViewById(R.id.reqPerDayText);
        daysToFinish = findViewById(R.id.daysToFinishText);
        goalTypeIcon = findViewById(R.id.goalTypeIcon);
        doneIcon = findViewById(R.id.doneIcon);
    }

    private void getGoalDetails() {
        final DocumentReference goalDoc = FirebaseFirestore.getInstance()
                .collection("goals").document(getIntent().getStringExtra("goalId"));

        goalDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();

                    if(snapshot.exists()) {
                        Map<String, Object> goalInfo = snapshot.getData();

                        // Set up Views
                        goalName.setText(goalInfo.get("goalName").toString());
                        targetMoney.setText(goalInfo.get("targetMoney").toString());
                        currentSavings.setText(goalInfo.get("currentSavings").toString());
                        reqPerDay.setText(goalInfo.get("requiredPerDay").toString());
                        daysToFinish.setText(goalInfo.get("remainingDays").toString());
                        getGoalTypeIcon(goalInfo.get("goalType").toString());
                        isGoalFinished((boolean) goalInfo.get("isFinished"));

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
    }

    private void checkIfCurrentGoal() {
        final DocumentReference userDoc = FirebaseFirestore.getInstance()
                .collection("users").document(currentUser.getUid());

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();

                    if(snapshot.exists()) {
                        Map<String, Object> userInfo = snapshot.getData();

                        String currentGoal = userInfo.get("currentGoal").toString();
                        if(currentGoal.equals(getIntent().getStringExtra("goalId"))) {
                            isCurrentGoal = true;
                        }
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
    }

    private void getGoalTypeIcon(String goalType) {
        if(goalType.equals("Just Cash Goal")) {
            goalTypeIcon.setImageResource(R.drawable.money);
        } else if(goalType.equals("Material Goal")) {
            goalTypeIcon.setImageResource(R.drawable.ps4);
        } else if(goalType.equals("Event Goal")) {
            goalTypeIcon.setImageResource(R.drawable.spotlights);
        }
    }

    private void isGoalFinished(boolean isFinished) {
        if(isFinished) {
            doneIcon.setImageResource(R.drawable.check);
        } else {
            doneIcon.setImageResource(R.drawable.cancel);
        }
    }

    public void cancel(View v) {
        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }

    public void deposit(View v) {
        if(isCurrentGoal) {
            Intent deposit = new Intent(getApplicationContext(), DepositActivity.class);
            startActivity(deposit);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "This is not your current goal.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void stopGoal(View v) {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid());
        userRef.update("currentGoal", "");

        Toast.makeText(getApplicationContext(), "Goal has been stopped.", Toast.LENGTH_LONG).show();

        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }

    public void delete(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Do you really wanna delete this goal?")
                .setIcon(R.drawable.warning)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DocumentReference docRef = FirebaseFirestore.getInstance().collection("goals")
                                .document(getIntent().getStringExtra("goalId"));
                        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
                                    startActivity(homeScr);
                                    finish();

                                    Toast.makeText(getApplicationContext(), "Goal is deleted.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        userRef = FirebaseFirestore.getInstance().collection("users")
                                .document(currentUser.getUid());
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot snapshot = task.getResult();

                                    if(snapshot.exists()) {
                                        Map<String, Object> userInfo = snapshot.getData();

                                        String currentGoalId = userInfo.get("currentGoal").toString();
                                        if(currentGoalId.equals(getIntent().getStringExtra("goalId"))) {
                                            userRef.update("currentGoal", "");
                                        }
                                    } else {

                                    }
                                } else {

                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", null).show();
    }
}
