package com.fenix.edict.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ReconnectJob extends JobService {
    public static final String TAG = "RECONN_JOB";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Network available!");
        SharedPreferences database = NetworkService.database;
        if (database != null && database.getBoolean("verified", false)) {
            Bundle extras = new Bundle();
            extras.putString("email", database.getString("email", null));
            extras.putString("password", database.getString("password", null));
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NetworkService.LOGIN).putExtras(extras));
        }
        jobFinished(jobParameters, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    public static void schedule(Context context) {
        ComponentName serviceComponent = new ComponentName(context, ReconnectJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if (jobScheduler != null)
            jobScheduler.schedule(builder.build());
    }
}
