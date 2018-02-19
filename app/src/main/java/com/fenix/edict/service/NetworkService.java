package com.fenix.edict.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NetworkService extends Service {
    public static final String TAG = "NET_SERVICE";

    public static final String REGISTER = "edict_register";
    public static final String LOGIN = "edict_login";
    public static final String LOGOUT = "edict_logout";

    public static Connection connection;
    private static Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Get database reference
        SharedPreferences localData = getSharedPreferences("database", 0);

        //Setup connection and executor thread
        new Thread(() -> {
            connection = new Connection();
            Looper.prepare();
            handler = new Handler();
            Looper.loop();
        }).start();

        //Create intent filter for service actions
        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(LOGIN);
        broadcastFilter.addAction(REGISTER);
        broadcastFilter.addAction(LOGOUT);

        //Register broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, broadcastFilter);

        //Log successful service startup
        Log.d(TAG, "Service ready...");

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
        handler.post(() -> connection.connect());
    }

    //Unregister receiver when service is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
