package com.saltsociety.kenyaku;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

public class SMSManager {

    private int REQUEST_SEND_SMS = 1;

    private Context context;
    private Activity activity;

    private PendingIntent sentPi, deliveredPi;

    public String SENT_CODE = "SMS_SENT";
    public String DELIVERED_CODE = "SMS_DELIVERED";

    public SMSManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        setUpIntentAndReceivers();
    }

    private void setUpIntentAndReceivers() {
        sentPi = PendingIntent.getBroadcast(context, 0, new Intent(SENT_CODE), 0);
        deliveredPi = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED_CODE), 0);
    }

    public void sendSms(String message, String number) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SEND_SMS);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, sentPi, deliveredPi);
        }
    }
}
