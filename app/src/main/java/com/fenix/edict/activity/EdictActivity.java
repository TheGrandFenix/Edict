package com.fenix.edict.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fenix.edict.R;
import com.fenix.edict.activity.chat.MessageAdapter;
import com.fenix.edict.filters.EdictIntentFilter;
import com.fenix.edict.service.NetworkService;
import com.fenix.support.Message;

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
        messageListView.addFooterView(new View(this), null, true);
        messageListView.setAdapter(messageAdapter);

        //Load a set of recent messages from memory
        Cursor cursor = NetworkService.getRecentMessages();
        if (cursor.moveToLast()) {
            Message lnewMessage = new Message();
            lnewMessage.messageId = cursor.getLong(cursor.getColumnIndex("MESSAGE_SERVER_ID"));
            lnewMessage.senderNick = cursor.getString(cursor.getColumnIndex("SENDER_NICK"));
            lnewMessage.text = cursor.getString(cursor.getColumnIndex("CONTENT"));
            messageAdapter.add(lnewMessage);
        }

        while (cursor.moveToPrevious()) {
            Message newMessage = new Message();
            newMessage.messageId = cursor.getLong(cursor.getColumnIndex("MESSAGE_SERVER_ID"));
            newMessage.senderNick = cursor.getString(cursor.getColumnIndex("SENDER_NICK"));
            newMessage.text = cursor.getString(cursor.getColumnIndex("CONTENT"));
            messageAdapter.add(newMessage);
        }

        //Register broadcast receiver for new messages
        broadcastManager.registerReceiver(broadcastReceiver, new EdictIntentFilter());

        //Set button action
        Button sendButton = findViewById(R.id.send_btn);
        sendButton.setOnClickListener(this::sendMessage);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(() -> {
            if (messageAdapter.getCount() > 0) {
                Log.d(TAG, "loadMore: start");
                Message lastMessage = messageAdapter.getItem(0);
                Cursor moreMessagesBefore = NetworkService.getMoreMessagesBefore(lastMessage.messageId);
                Log.d(TAG, "Loading: " + moreMessagesBefore.getCount());
                while (moreMessagesBefore.moveToNext()) {
                    Message newMessage = new Message();
                    newMessage.messageId = moreMessagesBefore.getLong(moreMessagesBefore.getColumnIndex("MESSAGE_SERVER_ID"));
                    newMessage.senderNick = moreMessagesBefore.getString(moreMessagesBefore.getColumnIndex("SENDER_NICK"));
                    newMessage.text = moreMessagesBefore.getString(moreMessagesBefore.getColumnIndex("CONTENT"));
                    Log.d(TAG, "loadMore: " + newMessage.messageId);
                    messageAdapter.insert(newMessage, 0);
                }
                messageListView.post(() -> messageListView.setSelection(messageAdapter.getPosition(lastMessage)));
            }
            refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onBackPressed() {
        //Do not react to back pressed
    }

    public void sendMessage(View view) {
        EditText message_input = findViewById(R.id.message_input_et);
        String input = message_input.getText().toString();
        if (!input.equals("")) {
            Log.d(TAG, "Sending message...");
            broadcastManager.sendBroadcast(new Intent(SEND_MESSAGE).putExtra("text", input));
            message_input.setText("");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, NetworkService.class));
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
