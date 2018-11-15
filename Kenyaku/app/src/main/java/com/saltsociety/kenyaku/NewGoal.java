package com.saltsociety.kenyaku;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewGoal extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DocumentReference userRef;

    // Views
    EditText goalNameText, targetMoneyText, daysToSaveText, targetDateText;

    // Data
    String goalType;
    byte[] goalTypeIcon;
    Calendar calendar;

    double allowancePerDay, foodExp, transpoExp, othersExp;
    long daysWithAllowance, totalGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);

        initializeAuth();
        getUser();
        getUserAllowanceInfo();

        fetchViews();
        setGoalType();
        restrictFieldsBaseOnGoalType();
        setupCalendar();
    }

    private void fetchViews() {
        goalNameText = findViewById(R.id.goalNameText);
        targetMoneyText = findViewById(R.id.targetMoneyText);
        daysToSaveText = findViewById(R.id.daysToSaveText);
        targetDateText = findViewById(R.id.targetDateText);
    }

    private void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void getUser() {
        currentUser = mAuth.getCurrentUser();
    }

    private void setGoalType() {
        // Set Goal Type
        TextView goalTypeText = findViewById(R.id.goalTypeLabel);
        goalType = getIntent().getStringExtra("goalType");
        goalTypeText.setText(goalType);

        // Set Icon
        Bundle extras = getIntent().getExtras();
        this.goalTypeIcon = extras.getByteArray("goalTypeIcon");

        Bitmap bmp = BitmapFactory.decodeByteArray(goalTypeIcon, 0, goalTypeIcon.length);
        ImageView goalTypeIcon = findViewById(R.id.goalTypeIcon);
        goalTypeIcon.setImageBitmap(bmp);
    }

    private void setupCalendar() {
        calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateTargetDate();
            }
        };

        targetDateText.setKeyListener(null);
        targetDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(NewGoal.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateTargetDate() {
        String dateFormatStr = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr, Locale.JAPAN);

        targetDateText.setText(dateFormat.format(calendar.getTime()));
    }

    private void restrictFieldsBaseOnGoalType() {
        if(goalType.equals("Just Cash Goal")) {
            daysToSaveText.setEnabled(false);
            targetDateText.setEnabled(false);
        } else if(goalType.equals("Material Goal")) {
            targetDateText.setEnabled(false);
        } else if(goalType.equals("Event Goal")) {
            daysToSaveText.setEnabled(false);
        }
    }

    private void getUserAllowanceInfo() {
        final DocumentReference userDocument = FirebaseFirestore.getInstance().document("users/" + currentUser.getUid());

        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Map<String, Object> userInfo = document.getData();
                        allowancePerDay = (double) userInfo.get("allowancePerDay");
                        daysWithAllowance = (long) userInfo.get("daysWithAllowance");
                        foodExp = (double) userInfo.get("foodExpenses");
                        transpoExp = (double) userInfo.get("transpoExpenses");
                        othersExp = (double) userInfo.get("othersExpenses");

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

    private byte[] feasibilityIcon(boolean isFeasible) {
        Bitmap bitmap;
        if(isFeasible) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cancel);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        return b;
    }

    private void saveGoal(String goalName, double targetMoney, int daysToFinish, double requiredPerDay,
                          Date startDate, Date endDate) {
        DocumentReference goalRef = FirebaseFirestore.getInstance().collection("goals").document();

        Map<String, Object> goalDetails = new HashMap<>();
        goalDetails.put("userId", currentUser.getUid());
        goalDetails.put("goalName", goalName);
        goalDetails.put("goalType", goalType);
        goalDetails.put("targetMoney", targetMoney);
        goalDetails.put("requiredPerDay", requiredPerDay);
        goalDetails.put("daysToFinish", daysToFinish);
        goalDetails.put("startDate", startDate);
        goalDetails.put("endDate", endDate);
        goalDetails.put("currentSavings", 0.00);
        goalDetails.put("leftToSave", targetMoney);
        goalDetails.put("remainingDays", daysToFinish);
        goalDetails.put("isFinished", false);

        goalRef.set(goalDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Successfully created a New Goal.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        userRef = FirebaseFirestore.getInstance().document("users/" + currentUser.getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();

                    if(snapshot.exists()) {
                        Map<String, Object> userInfo = snapshot.getData();
                        totalGoals = (long) userInfo.get("totalGoals");
                        userRef.update("totalGoals", totalGoals + 1);
                        Log.d("DEBUG", "" + totalGoals);
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

        userRef.update("currentGoal", goalRef.getId());

    }

    public void next(View v) {
        if (goalType.equals("Just Cash Goal")) {
            if (goalNameText.getText().toString().isEmpty() || targetMoneyText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_LONG).show();
            } else {
                CheckAllowance feasibility = new CheckAllowance(allowancePerDay, daysWithAllowance, foodExp, transpoExp, othersExp);

                String goalName = goalNameText.getText().toString();
                double targetMoney = Double.parseDouble(targetMoneyText.getText().toString());
                boolean isGoalFeasible = feasibility.checkIfGoalIsFeasible(goalType, targetMoney, 0);
                int daysToFinish = feasibility.daysToFinishGoal();

                String feasibilityLabel = "Not Feasible";
                if (isGoalFeasible) {
                    saveGoal(goalName, targetMoney, daysToFinish, feasibility.savePerDay(), new Date(), null);
                    feasibilityLabel = "Feasible";
                }

                // Redirect to goal feasibility
                Intent feasibilityScr = new Intent(getApplicationContext(), GoalFeasibility.class);
                feasibilityScr.putExtra("goalName", goalName);
                feasibilityScr.putExtra("goalType", goalType);
                feasibilityScr.putExtra("goalTypeIcon", goalTypeIcon);
                feasibilityScr.putExtra("savePerDay", feasibility.savePerDay());
                feasibilityScr.putExtra("daysToFinish", daysToFinish);
                feasibilityScr.putExtra("isGoalFeasible", feasibilityLabel);
                feasibilityScr.putExtra("feasibilityIcon", feasibilityIcon(isGoalFeasible));

                startActivity(feasibilityScr);
                finish();
            }
        } else if (goalType.equals("Material Goal")) {
            if (goalNameText.getText().toString().isEmpty() || targetMoneyText.getText().toString().isEmpty()
                    || daysToSaveText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_LONG).show();
            } else {
                CheckAllowance feasibility = new CheckAllowance(allowancePerDay, daysWithAllowance, foodExp, transpoExp, othersExp);

                String goalName = goalNameText.getText().toString();
                double targetMoney = Double.parseDouble(targetMoneyText.getText().toString());
                int daysToSave = Integer.parseInt(daysToSaveText.getText().toString());
                boolean isGoalFeasible = feasibility.checkIfGoalIsFeasible(goalType, targetMoney, daysToSave);

                String feasibilityLabel = "Not Feasible";
                if (isGoalFeasible) {
                    saveGoal(goalName, targetMoney, daysToSave, feasibility.requiredAllowancePerDay(), new Date(), null);
                    feasibilityLabel = "Feasible";
                }

                Intent feasibilityScr = new Intent(getApplicationContext(), GoalFeasibility.class);
                feasibilityScr.putExtra("goalName", goalName);
                feasibilityScr.putExtra("goalType", goalType);
                feasibilityScr.putExtra("goalTypeIcon", goalTypeIcon);
                feasibilityScr.putExtra("reqPerDay", feasibility.requiredAllowancePerDay());
                feasibilityScr.putExtra("savePerDay", feasibility.savePerDay());
                feasibilityScr.putExtra("daysToSave", daysToSave);
                feasibilityScr.putExtra("isGoalFeasible", feasibilityLabel);
                feasibilityScr.putExtra("feasibilityIcon", feasibilityIcon(isGoalFeasible));

                startActivity(feasibilityScr);
                finish();
            }
        } else if (goalType.equals("Event Goal")) {
            if (goalNameText.getText().toString().isEmpty() || targetMoneyText.getText().toString().isEmpty()
                    || targetDateText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_LONG).show();
            } else {
                CheckAllowance feasibility = new CheckAllowance(allowancePerDay, daysWithAllowance, foodExp, transpoExp, othersExp);

                String goalName = goalNameText.getText().toString();
                double targetMoney = Double.parseDouble(targetMoneyText.getText().toString());
                DateTime endDate = new DateTime(calendar.getTime());
                int daysToSave = feasibility.daysToSave(DateTime.now(), endDate);
                boolean isGoalFeasible = feasibility.checkIfGoalIsFeasible(goalType, targetMoney, daysToSave);

                String feasibilityLabel = "Not Feasible";
                if (isGoalFeasible) {
                    saveGoal(goalName, targetMoney, daysToSave, feasibility.requiredAllowancePerDay(), new Date(), calendar.getTime());
                    feasibilityLabel = "Feasible";
                }

                Intent feasibilityScr = new Intent(getApplicationContext(), GoalFeasibility.class);
                feasibilityScr.putExtra("goalName", goalName);
                feasibilityScr.putExtra("goalType", goalType);
                feasibilityScr.putExtra("goalTypeIcon", goalTypeIcon);
                feasibilityScr.putExtra("reqPerDay", feasibility.requiredAllowancePerDay());
                feasibilityScr.putExtra("savePerDay", feasibility.savePerDay());
                feasibilityScr.putExtra("daysToSave", daysToSave);
                feasibilityScr.putExtra("isGoalFeasible", feasibilityLabel);
                feasibilityScr.putExtra("feasibilityIcon", feasibilityIcon(isGoalFeasible));

                startActivity(feasibilityScr);
                finish();
            }
        }
    }

    public void cancel(View v) {
        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }

}
