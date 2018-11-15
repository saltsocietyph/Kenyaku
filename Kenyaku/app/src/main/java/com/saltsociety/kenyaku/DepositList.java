package com.saltsociety.kenyaku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DepositList extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ListView depositListView;
    private DepositAdapter depositAdapter;

    private ArrayList<String> goalIds = new ArrayList<>();
    private ArrayList<DepositItem> depositList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_list);

        initializeAuth();
        getUser();
        setUpDepositList();
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void setUpDepositList() {
        depositListView = findViewById(R.id.depositListView);
        getDepositDetails();
    }

    private void getDepositDetails() {
        final CollectionReference depositRef = FirebaseFirestore.getInstance().collection("deposits");
        depositRef.whereEqualTo("userId", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();

                    depositList = new ArrayList<>();
                    if(!snapshot.isEmpty()) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Map<String, Object> depositInfo = document.getData();

                            goalIds.add(document.getId());
                            depositList.add(new DepositItem(depositInfo.get("goalName").toString(),
                                    depositInfo.get("date").toString(), depositInfo.get("amount").toString()));

                            Log.d("DEBUG", "" + document.getData());
                        }

                        depositAdapter = new DepositAdapter(getApplicationContext(), depositList);
                        depositListView.setAdapter(depositAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "You haven't saved any money yet.",
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
        Intent vaultScr = new Intent(getApplicationContext(), GoalVault.class);
        startActivity(vaultScr);
        finish();
    }

    public void depositList(View v) {

    }

}
