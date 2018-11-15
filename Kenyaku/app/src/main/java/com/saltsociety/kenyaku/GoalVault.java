package com.saltsociety.kenyaku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GoalVault extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ListView goalListView;
    private GoalAdapter goalAdapter;

    private ArrayList<String> goalIds = new ArrayList<>();
    private ArrayList<GoalItem> goalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_vault);

        initializeAuth();
        getUser();
        setUpGoalList();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void setUpGoalList() {
        // Get ListView
        goalListView = findViewById(R.id.goalListView);

        // Get goal details
        getGoalDetails();
        goalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent goalInfo = new Intent(getApplicationContext(), GoalInfo.class);
                goalInfo.putExtra("goalId", goalIds.get(position));
                startActivity(goalInfo);
                finish();
            }
        });
    }

    private void getGoalDetails() {
        final CollectionReference goalDocs = FirebaseFirestore.getInstance().collection("goals");
        goalDocs.whereEqualTo("userId", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();

                    goalList = new ArrayList<>();
                    if(!snapshot.isEmpty()) {
                        for(DocumentSnapshot document: snapshot.getDocuments()) {
                            Map<String, Object> goalInfo = document.getData();

                            goalIds.add(document.getId());
                            goalList.add(new GoalItem(document.getId(), getGoalTypeIcons(goalInfo.get("goalType").toString()),
                                    goalInfo.get("goalName").toString(), goalInfo.get("startDate").toString()));
                        }

                        goalAdapter = new GoalAdapter(GoalVault.this, goalList);
                        goalListView.setAdapter(goalAdapter);

                    } else {
                        Toast.makeText(getApplicationContext(), "You have no goals as of the moment.",
                                Toast.LENGTH_LONG).show();

                        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
                        startActivity(homeScr);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private int getGoalTypeIcons(String goalType) {
        int goalTypeIcon = 0;

        if(goalType.equals("Just Cash Goal")) {
            goalTypeIcon =R.drawable.money;
        } else if(goalType.equals("Material Goal")) {
            goalTypeIcon = R.drawable.ps4;
        } else if(goalType.equals("Event Goal")) {
            goalTypeIcon =R.drawable.spotlights;
        }

        return goalTypeIcon;
    }

    public void menu(View v) {
        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }

    public void profile(View v) {
        Intent profileScr = new Intent(getApplicationContext(), Profile.class);
        startActivity(profileScr);
        finish();
    }

    public void vault(View v) {

    }

    public void depositList(View v) {
        Intent depositScr = new Intent(getApplicationContext(), DepositList.class);
        startActivity(depositScr);
        finish();
    }

}
