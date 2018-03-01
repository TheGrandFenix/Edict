package com.fenix.edict.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fenix.edict.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


public class EdictActivity extends Activity {
    public static final String TAG = "EDICT_ACT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edict);
    }

    @Override
    public void onBackPressed() {
        //Do not react to back pressed
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Subscribed to notifications...");
        FirebaseMessaging.getInstance().subscribeToTopic("info-board");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Unsubscribed from notifications...");
        super.onResume();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("info-board");


    }
}
