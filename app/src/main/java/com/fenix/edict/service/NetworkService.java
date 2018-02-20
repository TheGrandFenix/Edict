package com.fenix.edict.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

            //Establish connection to server
            handler.post(() -> connection.connect());

            //Get broadcast manager and register receiver
            broadcastManager = LocalBroadcastManager.getInstance(this);
            broadcastManager.registerReceiver(broadcastReceiver, new ServiceIntentFilter());

            //Log successful service startup
            Log.d(TAG, "Service ready...");
        }
        return START_STICKY;
    }

    //Define service broadcast actions
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) switch (intent.getAction()) {
                //Handle registration request
                case REGISTER:
                    handler.post(() -> connection.register(intent.getExtras()));
                    break;

                //Handle login request
                case LOGIN:
                    Log.d(TAG, "Attempting login...");
                    handler.post(() -> connection.login(intent.getExtras()));
                    break;

                //Handle logout request
                case LOGOUT:
                    handler.post(() -> connection.logout());
                    break;

                //Handle message sending request
                case SEND_MESSAGE:
                    handler.post(() -> connection.sendMessage(3, intent.getExtras().getString("message")));
                    break;
            }
        }
    };

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
