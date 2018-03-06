package com.fenix.edict.activity.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fenix.edict.R;
import com.fenix.support.Message;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //Get message from array
        Message message = getItem(position);

        //Create view for new message
        if(convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);

        //Get text views from message view
        TextView username = convertView.findViewById(R.id.sender_tv);
        TextView messageDisplay = convertView.findViewById(R.id.message_tv);

        //Set username and password
        username.setText(String.valueOf(message != null ? message.senderId : 0));
        messageDisplay.setText(message != null ? message.text : "missingtext");

        //Return created view
        return convertView;
    }
}
