package com.fenix.edict.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NetworkService extends Service {
    public static final String TAG = "NET_SERVICE";

    public static final String REGISTER = "edict_register";
    public static final String LOGIN = "edict_login";
    public static final String LOGOUT = "edict_logout";

    public static boolean serviceConnected = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Get database reference
        SharedPreferences localData = getSharedPreferences("database", 0);

        //Create intent filter for service actions
        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(LOGIN);
        broadcastFilter.addAction(REGISTER);
        broadcastFilter.addAction(LOGOUT);

        //Register broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, broadcastFilter);

        //Login with local details if verified
        if (localData.getBoolean("verified", false)) {
            String email = localData.getString("email", null);
            String password = localData.getString("password", null);
            if (email != null && password != null) attemptConnection(email, password);

        //Report service as idle if no login was performed
        } else
            Log.d(TAG, "Service idle...");

        return START_STICKY;
    }

    //Define service broadcast actions
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) switch (intent.getAction()) {
                case REGISTER:
                    break;

                case LOGIN:
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        String email = extras.getString("email", null);
                        String password = extras.getString("password", null);
                        if (email != null && password != null) attemptConnection(email, password);
                    }
                    break;

                case LOGOUT:
                    break;
            }
        }
    };

    //Connect to server using provided credentials
    private void attemptConnection(String email, String password) {
        Log.d(TAG, "Attempting connection...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
