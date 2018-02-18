package com.fenix.edict.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fenix.edict.service.NetworkService;

public class SplashActivity extends Activity {
    public static final String TAG = "SPLASH_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, NetworkService.class));

        //Check if user is verified
        Boolean verified = getSharedPreferences("database",0).getBoolean("verified", false);
        if (verified) {
            //Start Edict and Network Service
            startActivity(new Intent(this, EdictActivity.class));
        } else {
            //Start login procedure
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }

}
