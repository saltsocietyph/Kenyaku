package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

import java.text.DecimalFormat;
import java.util.Map;

public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    // Views
    TextView usernameLabel, totalGoalCountText, achievedGoalCountText, goalNameLabel, percentageLabel,
        currentGoalLabel, doneLabel;
    ImageView goalTypeIcon;
    String currentGoalId;

    DecimalFormat df = new DecimalFormat(".00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeAuth();
        getUser();
        fetchViews();
        setUpProfile();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void fetchViews() {
        usernameLabel = findViewById(R.id.usernameLabel);
        totalGoalCountText = findViewById(R.id.totalGoalCount);
        achievedGoalCountText = findViewById(R.id.achievedGoalCount);
        goalNameLabel = findViewById(R.id.goalNameLabel);
        percentageLabel = findViewById(R.id.percentage);
        currentGoalLabel = findViewById(R.id.currentGoalLabel);
        doneLabel = findViewById(R.id.doneLabel);
        goalTypeIcon = findViewById(R.id.currentGoalIcon);
    }

    private void setUpProfile() {
        final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();

                    if(snapshot.exists()) {
                        Map<String, Object> userInfo = snapshot.getData();

                        usernameLabel.setText(userInfo.get("username").toString());
                        totalGoalCountText.setText(userInfo.get("totalGoals").toString());
                        achievedGoalCountText.setText(userInfo.get("achievedGoals").toString());
                        currentGoalId = userInfo.get("currentGoal").toString();

                        if(currentGoalId != "") {
                            goalNameLabel.setVisibility(View.VISIBLE);
                            percentageLabel.setVisibility(View.VISIBLE);
                            currentGoalLabel.setVisibility(View.VISIBLE);
                            goalTypeIcon.setVisibility(View.VISIBLE);
                            doneLabel.setVisibility(View.VISIBLE);
                            setUpCurrentGoal();
                        } else {
                            goalNameLabel.setVisibility(View.INVISIBLE);
                            percentageLabel.setVisibility(View.INVISIBLE);
                            currentGoalLabel.setVisibility(View.INVISIBLE);
                            goalTypeIcon.setVisibility(View.INVISIBLE);
                            doneLabel.setVisibility(View.INVISIBLE);
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

    private void setUpCurrentGoal() {
        final DocumentReference goalDoc = FirebaseFirestore.getInstance().collection("goals")
                .document(currentGoalId);

        goalDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();

                    if(snapshot.exists()) {
                        Map<String, Object> goalInfo = snapshot.getData();

                        goalNameLabel.setText(goalInfo.get("goalName").toString());

                        double targetMoney = (double) goalInfo.get("targetMoney");
                        double currentSavings = (double) goalInfo.get("currentSavings");
                        double percentage = (currentSavings / targetMoney) * 100;

                        percentageLabel.setText("" + df.format(percentage) + "%");
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
    // Button Functions
    // Log-Out
    public void logout(View v) {
        FirebaseAuth.getInstance().signOut();

        // Redirect to Log In Screen
        Intent loginScr = new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(loginScr);
        finish();
    }

    // Menu Buttons
    public void menu(View v) {
        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }

    public void vault(View v) {
        Intent vaultScr = new Intent(getApplicationContext(), GoalVault.class);
        startActivity(vaultScr);
        finish();
    }

    public void depositList(View v) {
        Intent depositScr = new Intent(getApplicationContext(), DepositList.class);
        startActivity(depositScr);
        finish();
    }

    public void profile(View v) {

    }
}
