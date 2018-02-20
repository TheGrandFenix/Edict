package com.fenix.edict.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fenix.edict.filters.LoginIntentFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.fenix.edict.activity.LoginActivity.LOGIN_ACK;
import static com.fenix.edict.activity.LoginActivity.LOGIN_ERR;

public class Connection {
    private static final String TAG = "CONN";

    private static final int REGISTRATION_REQUEST = 0;
    private static final int LOGIN_REQUEST = 1;
    private static final int LOGOUT_REQUEST = 2;
    private static final int LOGIN_SUCCESS = 3;
    private static final int LOGIN_ERROR = 4;
    private static final int TEXT_MESSAGE = 5;

    private static final InetSocketAddress address = new InetSocketAddress("192.168.1.44", 2508);

    private Socket socket;

    private BufferedWriter output;
    private BufferedReader input;

    private HandlerThread execThread;
    private Handler execHandler;
    private HandlerThread inThread;
    private Handler inHandler;

    public boolean isConnected = false;
    public boolean isLoggedIn = false;

    Connection() {
        //Create executor thread
        execThread = new HandlerThread("conn_exec_thread");
        execThread.start();
        execHandler = new Handler(execThread.getLooper());

        //Create thread for receiving server messages
        inThread = new HandlerThread("conn_in_thread");
        inThread.start();
        inHandler = new Handler(inThread.getLooper());
    }

    //Attempt socket connection to the server
    private void connect() {
        execHandler.post(() -> {
            Log.d(TAG, "Attempting connection...");
            try {
                //Create socket connected to the server
                socket = new Socket();
                socket.connect(address, 1000);
                output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                inHandler.post(this::listen);
                Log.d(TAG, "Successfully connected to server...");
            } catch (IOException e) {
                //Log connection failure
                isConnected = false;
                Log.d(TAG, "Failed to connect to server...\n" + e);
            }
        });
    }

    void login(Bundle extras) {
        if (!isConnected) connect();
        String email = extras.getString("email");
        String password = extras.getString("password");

        String serverAuth = email + "[#]" + password;

        sendMessage(LOGIN_REQUEST, serverAuth);
    }

    void register(Bundle extras) {
        if (!isConnected) connect();
        String email = extras.getString("email");
        String password = extras.getString("password");

        String serverAuth = email + "[#]" + password;

        sendMessage(REGISTRATION_REQUEST, serverAuth);
    }

    //Send message with content to server [full message]
    void sendMessage(int messageType, String message) {
        if (isConnected && output != null) try {
            output.write(messageType);
            output.write(message);
            output.newLine();
            output.flush();
        } catch (IOException e) {
            Log.d(TAG, "Failed to send message, disconnecting...");
            logout();
            disconnect();
        }
    }

    //Listen for incoming server messages
    private void listen() {
        while (isConnected) {
            try {
                //Receive message type and content
                int messageType = input.read();
                String message = input.readLine();

                //Handle message
                execHandler.post(() -> handleMessage(messageType, message));

            } catch (IOException e) {
                isConnected = false;
                Log.d(TAG, "Failed to read message from server, disconnecting...");
                logout();
                disconnect();
            }
        }
    }

    //Process received message
    private void handleMessage(int messageType, String message) {
        Log.d(TAG, "Received message...");
        switch (messageType) {
            case LOGIN_SUCCESS:
                Log.d(TAG, "Login success!");
                isLoggedIn = true;
                NetworkService.broadcastManager.sendBroadcast(new Intent(LOGIN_ACK));
                break;

            case LOGIN_ERROR:
                Log.d(TAG, "Login error!");
                isLoggedIn = false;
                NetworkService.broadcastManager.sendBroadcast(new Intent(LOGIN_ERR));
                break;

            case TEXT_MESSAGE:
                //NetworkService.broadcastManager.sendBroadcast(NEW_MESSAGE);
                break;
        }
    }

    //Send logout request to server
    void logout() {
        sendMessage(LOGOUT_REQUEST, "logout");
        isLoggedIn = false;
    }

    //Close connection to server
    void disconnect() {
        try {
            //Close streams and socket
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null) socket.close();
            Log.d(TAG, "Successfully closed socket...");
        } catch (IOException e) {
            Log.d(TAG, "Failed to manually close socket..." + e);
        }

        //Update connected state
        isLoggedIn = false;
        isConnected = false;
    }

    //Stop input and executor thread before ending this this thread
    void stopThreads() {
        if (execThread != null) execThread.quit();
        if (inThread != null) inThread.quit();
        Log.d(TAG, "Closed local threads...");
    }
}
