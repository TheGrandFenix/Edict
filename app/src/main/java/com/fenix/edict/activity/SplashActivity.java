package com.fenix.edict.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fenix.edict.service.NetworkService;

public class SplashActivity extends Activity {
    public static final String TAG = "SPLASH_ACT";

    private Boolean verified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Check if user is verified
        verified = getSharedPreferences("database",0).getBoolean("verified", false);
        if (verified) {
            //Start Edict and Network Service
            Intent intent = new Intent(this, NetworkService.class);
            startService(intent);
            intent = new Intent(this, EdictActivity.class);
            startActivity(intent);
        } else {
            //Start login procedure
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        finish();
    }

}
