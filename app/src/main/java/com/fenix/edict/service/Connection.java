package com.fenix.edict.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Connection {
    public static final String TAG = "CONN";

    private static final String address = "10.0.2.2";
    private static final int port = 2508;

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private HandlerThread execThread;
    private Handler execHandler;
    private HandlerThread outThread;
    private Handler outHandler;
    private HandlerThread inThread;
    private Handler inHandler;

    public static boolean isConnected = false;
    public static boolean isLoggedIn = false;

    Connection() {
        //Create executor thread
        execThread = new HandlerThread("conn_exec_thread");
        execHandler = new Handler(execThread.getLooper());
    }

    //Attempt socket connection to the server
    void connect() {
        Log.d(TAG, "Attempting connection...");
        try {
            //Create socket connected to the server
            socket = new Socket(address, port);
            Log.d(TAG, "Successfully connected to server...");

            //Create thread for sending messages to server
            outThread = new HandlerThread("conn_out_thread");
            outHandler = new Handler(outThread.getLooper());

            //Create thread for receiving server messages
            inThread = new HandlerThread("conn_in_thread");
            inHandler = new Handler(inThread.getLooper());
        } catch (IOException e) {
            //Log connection failure
            isConnected = false;
            Log.d(TAG, "Failed to connect to server..." + e);
        }
    }

    void login(Bundle extras) {

    }

    void register(Bundle extras) {

    }

    void logout() {

    }

    void disconnect() {

    }
}
