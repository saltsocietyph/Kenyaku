package com.saltsociety.kenyaku;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME = 3000;

    // Views
    TextView developerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Set up custom fonts
        customFont();

        // Delay next intent for 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loadingScr = new Intent(getApplicationContext(), Loading.class);
                startActivity(loadingScr);
                finish();
            }
        }, SPLASH_TIME);
    }

    private void customFont() {
        Typeface heroF = Typeface.createFromAsset(getAssets(), "fonts/Hero.otf");

        // Get developer name text view
        developerName = findViewById(R.id.developerName);
        // Set font
        developerName.setTypeface(heroF);
    }
}
