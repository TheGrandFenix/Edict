package com.fenix.edict.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fenix.edict.database.SQLiteDB;
import com.fenix.edict.filters.ServiceIntentFilter;
import com.fenix.support.Message;

import static com.fenix.edict.service.Connection.TEXT_MESSAGE;
import static java.security.AccessController.getContext;

public class NetworkService extends Service {
    private static final String TAG = "NET_SERVICE";

    public static final String REGISTER = "edict_register";
    public static final String LOGIN = "edict_login";
    public static final String LOGOUT = "edict_logout";
    public static final String SEND_MESSAGE = "edict_send";

    public Connection connection;
    private static HandlerThread handlerThread;
    private static Handler handler;

    static LocalBroadcastManager broadcastManager;
    static SharedPreferences database;
    private static SQLiteDB dbHelper;
    static SQLiteDatabase sqliteDatabase;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (connection == null) {
            //Create connection
            connection = new Connection(this);

            //Create network thread
            handlerThread = new HandlerThread("network_thread");
            handlerThread.start();

            //Get network thread handler
            handler = new Handler(handlerThread.getLooper());

            //Get broadcast manager and register receiver
            broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
            broadcastManager.registerReceiver(broadcastReceiver, new ServiceIntentFilter());

            //Attempt login if verified
            attemptLogin();
        }

        if (sqliteDatabase == null) {
            dbHelper = new SQLiteDB(this);
            sqliteDatabase = dbHelper.getWritableDatabase();
        }

        //Log successful service startup
        Log.d(TAG, "Service ready...");

        return START_NOT_STICKY;
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
                    handler.post(() -> {
                        Log.d(TAG, "Processing message sending from service...");
                        Message newMessage = new Message();
                        newMessage.text = intent.getExtras().getString("text");
                        newMessage.senderId = 256;
                        connection.sendMessage(TEXT_MESSAGE, newMessage);
                    });
                    break;
            }
        }
    };

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

        //Close database connection
        dbHelper.close();

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

        //Close database connection
        dbHelper.close();

        Log.d(TAG, "Service destroyed...");
    }

}
