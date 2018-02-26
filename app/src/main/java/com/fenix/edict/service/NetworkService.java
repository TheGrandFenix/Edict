package com.fenix.edict.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fenix.edict.activity.EdictActivity;
import com.fenix.edict.filters.ServiceIntentFilter;

public class NetworkService extends Service {
    private static final String TAG = "NET_SERVICE";

    public static final String REGISTER = "edict_register";
    public static final String LOGIN = "edict_login";
    public static final String LOGOUT = "edict_logout";
    public static final String SEND_MESSAGE = "edict_send";

    public static Connection connection;
    private static HandlerThread handlerThread;
    private static Handler handler;

    static LocalBroadcastManager broadcastManager;
    static SharedPreferences database;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (connection == null) {
            //Create connection
            connection = new Connection();

            //Create network thread
            handlerThread = new HandlerThread("network_thread");
            handlerThread.start();

            //Get network thread handler
            handler = new Handler(handlerThread.getLooper());

            //Get broadcast manager and register receiver
            broadcastManager = LocalBroadcastManager.getInstance(this);
            broadcastManager.registerReceiver(broadcastReceiver, new ServiceIntentFilter());

            //Log successful service startup
            Log.d(TAG, "Service ready...");

            //Attempt login if verified
            attemptLogin();
        }
        return START_STICKY;
    }

    //Define service broadcast actions
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getExtras() != null) switch (intent.getAction()) {
                //Handle registration request
                case REGISTER:
                    Log.d(TAG, "Attempting registration...");
                    handler.post(() -> connection.register(intent.getExtras()));
                    break;

                //Handle login request
                case LOGIN:
                    Log.d(TAG, "Attempting login...");
                    handler.post(() -> connection.login(intent.getExtras()));
                    break;

                //Handle logout request
                case LOGOUT:
                    Log.d(TAG, "Logging out...");
                    handler.post(() -> connection.logout());
                    database.edit().putBoolean("verified", false).apply();
                    break;

                //Handle message sending request
                case SEND_MESSAGE:
                    handler.post(() -> connection.sendMessage(3, intent.getExtras().getString("message")));
                    break;

                //Handle network changes
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    checkConnectivity();
                    break;
            }
        }
    };

    //Check if network connection has been restored
    private void checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) attemptLogin();
    }

    private void attemptLogin() {
        //Attempt login if user was verified
        database = getSharedPreferences("database", 0);
        if (database.getBoolean("verified", false)) {
            Bundle extras = new Bundle();
            extras.putString("email", database.getString("email", null));
            extras.putString("password", database.getString("password", null));
            handler.post(() -> connection.login(extras));
        }
    }

    //Cleanup when service is reset
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        //Unregister receiver
        broadcastManager.unregisterReceiver(broadcastReceiver);

        //Disconnect from server
        connection.disconnect();
        connection.stopThreads();

        //Ends network thread
        handlerThread.quit();

        Log.d(TAG, "Service destroyed...");
    }

    //Cleanup when service is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();

        //Unregister receiver
        broadcastManager.unregisterReceiver(broadcastReceiver);

        //Disconnect from server
        connection.disconnect();
        connection.stopThreads();

        //Ends network thread
        handlerThread.quit();

        Log.d(TAG, "Service destroyed...");
    }


}
