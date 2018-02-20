package com.fenix.edict.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.fenix.edict.filters.LoginIntentFilter;
import com.fenix.edict.service.NetworkService;

import static com.fenix.edict.activity.LoginActivity.LOGIN_ACK;
import static com.fenix.edict.service.NetworkService.LOGIN;

public class SplashActivity extends Activity {
    private static final String TAG = "SPLASH_ACT";

    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(broadcastReceiver, new LoginIntentFilter());

        startService(new Intent(this, NetworkService.class));

        //Start login procedure if unverified
        if (!getSharedPreferences("database", 0).getBoolean("verified", false)) {
            broadcastManager.unregisterReceiver(broadcastReceiver);
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) switch (intent.getAction()) {
                case LOGIN_ACK:
                    broadcastManager.unregisterReceiver(broadcastReceiver);
                    startActivity(new Intent(getApplicationContext(), EdictActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                    break;
            }
        }
    };

}
