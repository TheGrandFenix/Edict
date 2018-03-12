package com.fenix.edict.activity.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenix.edict.R;
import com.fenix.edict.service.NetworkService;
import com.fenix.support.Message;

import java.util.ArrayList;


public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //Get message from array
        Message message = getItem(position);
        Message previousMessage = null;
        if (position > 0) previousMessage = getItem(position - 1);
        boolean showUsername = true;
        if (previousMessage != null && message.senderNick.equals(previousMessage.senderNick)) showUsername = false;

        //Create view for new message
        if(convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);

        //Get text views from message view
        TextView username = convertView.findViewById(R.id.sender_tv);
        TextView messageDisplay = convertView.findViewById(R.id.message_tv);

        //If self sent message -> place username on right
        if (message != null && message.senderNick.equals(NetworkService.username)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(username.getLayoutParams());
            params.gravity = Gravity.END;
            username.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(username.getLayoutParams());
            params.gravity = Gravity.START;
            username.setLayoutParams(params);
        }

        //Set username and password
        username.setText(message != null ? message.senderNick : "missingname");
        messageDisplay.setText(message != null ? message.text : "missingtext");

        username.setVisibility(showUsername ? View.VISIBLE : View.GONE);

        //Return created view
        return convertView;
    }
}
