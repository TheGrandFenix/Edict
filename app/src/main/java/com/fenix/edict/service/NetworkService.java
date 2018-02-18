package com.fenix.edict.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.fenix.edict.activity.EdictActivity;


public class NetworkService extends Service {
    public static final String TAG = "NET_SERVICE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            //Read login details
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String email = extras.getString("email");
                String password = extras.getString("password");
                attemptConnection(email, password);
            }
        } else {
            String auth = getSharedPreferences("database", 0).getString("auth", "");
            if (!auth.equals(""))
                attemptConnection(auth);
            else
                Log.d("TAG", "Service idle...");
        }

        return START_STICKY;
    }

    private void attemptConnection(String email, String password) {

    }

    private void attemptConnection(String auth) {

    }
}
