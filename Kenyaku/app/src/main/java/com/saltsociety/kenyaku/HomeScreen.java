package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class HomeScreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    // Views
    TextView subtitle;

    // Data
    boolean isCurrentGoalEmpty = true;
    String currentGoalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // customFont();
        initializeAuth();
        getUser();
        isCurrentGoalEmpty();
    }

    private void customFont() {
        Typeface heroF = Typeface.createFromAsset(getAssets(), "fonts/Hero.otf");

        // Get text view
        subtitle = findViewById(R.id.subtitle);
        // Set font
        subtitle.setTypeface(heroF);
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void isCurrentGoalEmpty() {
        DocumentReference userDocument = FirebaseFirestore.getInstance().document("users/" + currentUser.getUid());

        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Map<String, Object> userInfo = document.getData();

                        // Can only create 1 goal at a time
                        if(userInfo.get("currentGoal").toString().isEmpty() || userInfo.get("currentGoal").toString() == "") {
                            isCurrentGoalEmpty = true;
                        } else {
                            isCurrentGoalEmpty = false;
                            currentGoalId = userInfo.get("currentGoal").toString();
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
    }

    // Menu Buttons
    public void menu(View v) {

    }

    public void profile(View v) {
        Intent profileScr = new Intent(getApplicationContext(), Profile.class);
        startActivity(profileScr);
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

    // Home Buttons
    public void createAGoal(View v) {
        if(isCurrentGoalEmpty) {
            // Redirect to Choose a Goal Type
            Intent chooseGoalType = new Intent(getApplicationContext(), ChooseACategory.class);
            startActivity(chooseGoalType);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! You can only have one on-going goal at a time.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void deposit(View v) {
        if(!isCurrentGoalEmpty) {

            Intent depositScr = new Intent(getApplicationContext(), DepositActivity.class);
            startActivity(depositScr);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "You don't have a goal.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
