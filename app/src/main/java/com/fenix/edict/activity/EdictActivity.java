package com.fenix.edict.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fenix.edict.R;
import com.fenix.edict.activity.chat.MessageAdapter;
import com.fenix.edict.filters.EdictIntentFilter;
import com.fenix.support.Message;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;

import static com.fenix.edict.service.NetworkService.SEND_MESSAGE;


public class EdictActivity extends Activity {
    public static final String TAG = "EDICT_ACT";

    public static final String NEW_MESSAGE = "edict.receiveMessage";

    private MessageAdapter messageAdapter;

    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edict);

        //Get broadcast manager
        broadcastManager = LocalBroadcastManager.getInstance(this);

        //Create arraylist for messages
        ArrayList<Message> messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);

        //Set custom adapter
        ListView messageListView = findViewById(R.id.messages_list);
        messageListView.setAdapter(messageAdapter);

        broadcastManager.registerReceiver(broadcastReceiver, new EdictIntentFilter());

        Button sendButton = findViewById(R.id.send_btn);
        sendButton.setOnClickListener(this::sendMessage);
    }

    @Override
    public void onBackPressed() {
        //Do not react to back pressed
    }

    public void sendMessage(View view) {
        Log.d(TAG, "Sending message...");
        EditText message_input = findViewById(R.id.message_input_et);
        broadcastManager.sendBroadcast(new Intent(SEND_MESSAGE).putExtra("text", message_input.getText().toString()));
        message_input.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Subscribe to notifications when app is put into background
        Log.d(TAG, "Subscribed to notifications...");
        FirebaseMessaging.getInstance().subscribeToTopic("info-board");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Un-subscribe from notifications when app is resumed
        Log.d(TAG, "Un-subscribed from notifications...");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("info-board");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Get message object from intent
            Message message = (Message) intent.getSerializableExtra("messageObject");

            //Add message to UI
            messageAdapter.add(message);
        }
    };

    @Override
    protected void onDestroy() {
        //Unregister receiver
        broadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
