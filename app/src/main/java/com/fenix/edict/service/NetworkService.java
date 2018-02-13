package com.fenix.edict.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class NetworkService extends Service {
    public static final String TAG = "NET_SERVICE";

    private String email;
    private String password;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            //Read login details
            Bundle extras = intent.getExtras();
            assert extras != null;
            email = extras.getString("email");
            password = extras.getString("password");
            attemptConnection(email, password);
        } else {
            email = getSharedPreferences("database", 0).getString("auth", "");
            if (!email.equals("") && !password.equals(""))
                attemptConnection(email, password);
            else
                Log.d("TAG", "Service idle...");
        }

        Log.d(TAG, email + " " + password);

        return START_STICKY;
    }

    private void attemptConnection(String email, String password) {

    }

    private void attemptAuthConnection(String auth) {

    }
}
