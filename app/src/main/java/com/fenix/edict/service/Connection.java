package com.fenix.edict.service;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Connection {
    public static final String TAG = "CONN";

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public static boolean isConnected = false;

    void connect() {
        try {
            socket = new Socket("10.0.2.2", 2508);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            String ack = inputStream.readUTF();
            if (ack.equals("CONNECTED")) {
                isConnected = true;
                Log.d(TAG, "Connected to server...");
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to connect to server...\n" + e);
        }
    }
}
