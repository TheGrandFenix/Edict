package com.fenix.edict.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "NOTIFICATION_SERVICE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Un-subscribed from notifications...");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("info-board");
    }
}
