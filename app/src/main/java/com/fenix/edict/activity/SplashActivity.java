package com.fenix.edict.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.fenix.edict.service.NetworkService;

import static com.fenix.edict.service.NetworkService.LOGIN;

public class SplashActivity extends Activity {
    public static final String TAG = "SPLASH_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, NetworkService.class));

        SharedPreferences database = getSharedPreferences("database",0);

        //Check if user is verified
        Boolean verified = database.getBoolean("verified", false);
        if (verified) {
            //Create login intent
            Intent intent = new Intent(LOGIN);
            Bundle extras = new Bundle();
            extras.putString("email", database.getString("email", null));
            extras.putString("password", database.getString("password", null));
            intent.putExtras(extras);

            //Request login from service
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            //Start edict activity
            startActivity(new Intent(this, EdictActivity.class));
        } else {
            //Start login procedure
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }

}
