package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GoalFeasibility extends AppCompatActivity {

    // Views
    ImageView goalTypeIcon, feasibilityIcon;
    TextView goalTypeLabel, goalNameLabel, goalFeasibilityLabel;
    EditText reqPerDayText, savePerDayText, daysToFinishText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_feasibility);

        fetchViews();
        setUpViews();
    }

    private void fetchViews() {
        goalTypeIcon = findViewById(R.id.goalTypeIcon);
        feasibilityIcon = findViewById(R.id.feasibilityIcon);
        goalTypeLabel = findViewById(R.id.goalTypeLabel);
        goalFeasibilityLabel = findViewById(R.id.feasibilityText);
        goalNameLabel = findViewById(R.id.goalNameLabel);
        reqPerDayText = findViewById(R.id.requiredPerDayText);
        savePerDayText = findViewById(R.id.savePerDayText);
        daysToFinishText = findViewById(R.id.daysToFinishText);
    }

    private void setUpViews() {
        // Set Icons
        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("goalTypeIcon");
        Bitmap bmp =  BitmapFactory.decodeByteArray(b, 0, b.length);
        goalTypeIcon.setImageBitmap(bmp);

        b = extras.getByteArray("feasibilityIcon");
        bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        feasibilityIcon.setImageBitmap(bmp);

        // Set Texts
        goalNameLabel.setText(getIntent().getStringExtra("goalName"));
        goalTypeLabel.setText(getIntent().getStringExtra("goalType"));
        goalFeasibilityLabel.setText(getIntent().getStringExtra("isGoalFeasible"));

        DecimalFormat decimalFormat = new DecimalFormat(".00");
        if(getIntent().getStringExtra("goalType").equals("Just Cash Goal")) {
            double savePerDay = getIntent().getDoubleExtra("savePerDay", 0.0);
            reqPerDayText.setText("" + decimalFormat.format(savePerDay));
            savePerDayText.setText("" + decimalFormat.format(savePerDay));
            daysToFinishText.setText("" + getIntent().getIntExtra("daysToFinish", 0));
        } else if(getIntent().getStringExtra("goalType").equals("Material Goal")) {
            double savePerDay = getIntent().getDoubleExtra("savePerDay", 0.0);
            double reqPerDay = getIntent().getDoubleExtra("reqPerDay", 0.0);

            reqPerDayText.setText("" + decimalFormat.format(reqPerDay));
            savePerDayText.setText("" + decimalFormat.format(savePerDay));
            daysToFinishText.setText("" + getIntent().getIntExtra("daysToSave", 0));
        } else if(getIntent().getStringExtra("goalType").equals("Event Goal")) {
            double savePerDay = getIntent().getDoubleExtra("savePerDay", 0.0);
            double reqPerDay = getIntent().getDoubleExtra("reqPerDay", 0.0);

            reqPerDayText.setText("" + decimalFormat.format(reqPerDay));
            savePerDayText.setText("" + decimalFormat.format(savePerDay));
            daysToFinishText.setText("" + getIntent().getIntExtra("daysToSave", 0));
        }
    }

    public void done(View v) {
        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }
}
