package com.fenix.edict.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NetworkService extends Service {
    public static final String TAG = "NET_SERVICE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences localData = getSharedPreferences("database", 0);

        //Read login details from intent if available
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String email = extras.getString("email", null);
                String password = extras.getString("password", null);
                if (email != null && password != null) attemptConnection(email, password);
            }

        //Login with local details if service was run with no intent
        } else if (localData.getBoolean("verified", false)) {
            String email = localData.getString("email", null);
            String password = localData.getString("password", null);
            if (email != null && password != null) attemptConnection(email, password);

        //Mark service as idle if no login was performed
        } else
            Log.d(TAG, "Service idle...");

        return START_STICKY;
    }

    //Connect to server using provided credentials
    private void attemptConnection(String email, String password) {
        Log.d(TAG, "Attempting connection...");
    }
}
