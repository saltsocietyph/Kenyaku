package com.saltsociety.kenyaku;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class SMSJobService extends JobService {
    private static final String TAG = "SMSJobService";
    private boolean jobCancelled = false;

    public SMSJobService(String jobToDo) {

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started.");



        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void sendSMSNotification() {

    }
}
