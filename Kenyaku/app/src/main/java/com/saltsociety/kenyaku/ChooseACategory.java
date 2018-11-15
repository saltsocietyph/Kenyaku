package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class ChooseACategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_acategory);
    }

    public void cancel(View v) {
        Intent homeScr = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(homeScr);
        finish();
    }

    public void justCashGoal(View v) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.money);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        Intent justCashScr = new Intent(getApplicationContext(), NewGoal.class);
        justCashScr.putExtra("goalTypeIcon", b);
        justCashScr.putExtra("goalType", "Just Cash Goal");
        startActivity(justCashScr);
        finish();
    }

    public void materialGoal(View v) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ps4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        Intent materialScr = new Intent(getApplicationContext(), NewGoal.class);
        materialScr.putExtra("goalTypeIcon", b);
        materialScr.putExtra("goalType", "Material Goal");
        startActivity(materialScr);
        finish();
    }

    public void eventGoal(View v) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spotlights);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        Intent eventScr = new Intent(getApplicationContext(), NewGoal.class);
        eventScr.putExtra("goalTypeIcon", b);
        eventScr.putExtra("goalType", "Event Goal");
        startActivity(eventScr);
        finish();
    }
}
