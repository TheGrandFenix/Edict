package com.fenix.edict.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

class Connection {
    private static final String TAG = "CONN";

    public static final int REGISTRATION_REQUEST = 0;
    public static final int LOGIN_REQUEST = 1;
    public static final int LOGOUT_REQUEST = 2;
    public static final int TEXT_MESSAGE = 3;

    private static final InetSocketAddress address = new InetSocketAddress("home.edict.cc", 2508);

    private Socket socket;

    private BufferedWriter output;
    private BufferedReader input;

    private HandlerThread execThread;
    private Handler execHandler;
    private HandlerThread inThread;
    private Handler inHandler;

    public static boolean isConnected = false;
    public static boolean isLoggedIn = false;

    Connection() {
        //Create executor thread
        execThread = new HandlerThread("conn_exec_thread");
        execHandler = new Handler(execThread.getLooper());

        //Create thread for receiving server messages
        inThread = new HandlerThread("conn_in_thread");
        inHandler = new Handler(inThread.getLooper());
    }

    //Attempt socket connection to the server
    void connect() {
        execHandler.post(() -> {
            Log.d(TAG, "Attempting connection...");
            try {
                //Create socket connected to the server
                socket = new Socket();
                socket.connect(address, 1000);
                output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.d(TAG, "Successfully connected to server...");
            } catch (IOException e) {
                //Log connection failure
                isConnected = false;
                Log.d(TAG, "Failed to connect to server...\n" + e);
            }
        });
    }

    void login(Bundle extras) {
        String email = extras.getString("email");
        String password = extras.getString("password");
        String serverAuth = email + "[#]" + password;
        sendMessage(LOGIN_REQUEST, serverAuth);
    }

    void register(Bundle extras) {
        String email = extras.getString("email");
        String password = extras.getString("password");
        String serverAuth = email + "[#]" + password;
        sendMessage(REGISTRATION_REQUEST, serverAuth);
    }

    void sendMessage(int messageType, String message) {
        try {
            output.write(messageType);
            output.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void logout() {
        sendMessage(LOGOUT_REQUEST, "logout");
    }

    void disconnect() {
        try {
            //Close streams and socket
            output.close();
            input.close();
            socket.close();
            Log.d(TAG, "Successfully closed socket...");
        } catch (IOException e) {
            Log.d(TAG, "Failed to manually close socket..." + e);
        }

        //Update connected state
        isConnected = false;
    }

    void stopThreads() {
        if (execThread != null) execThread.quit();
        if (inThread != null) inThread.quit();
        Log.d(TAG, "Closed local threads...");
    }
}
