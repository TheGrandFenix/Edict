package com.fenix.edict.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.fenix.support.LoginRequest;
import com.fenix.support.Message;
import com.fenix.support.RegistrationRequest;
import com.fenix.support.ServerResponse;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.fenix.edict.activity.EdictActivity.NEW_MESSAGE;
import static com.fenix.edict.activity.LoginActivity.*;

public class Connection {
    private static final String TAG = "CONN";

    private static final int REGISTRATION_REQUEST = 0;
    private static final int LOGIN_REQUEST = 1;
    private static final int LOGOUT_REQUEST = 2;
    private static final int LOGIN_SUCCESS = 3;
    private static final int LOGIN_ERROR = 4;
    static final int TEXT_MESSAGE = 5;

    private Context serviceContext;

    private final static String address = ("192.168.1.25");

    private Socket socket;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private HandlerThread execThread;
    private Handler execHandler;
    private HandlerThread inThread;
    private Handler inHandler;

    private static boolean isConnected = false;
    public static boolean isLoggedIn = false;

    Connection(Context context) {
        //Create executor thread
        execThread = new HandlerThread("conn_exec_thread");
        execThread.start();
        execHandler = new Handler(execThread.getLooper());

        //Create thread for receiving server messages
        inThread = new HandlerThread("conn_in_thread");
        inThread.start();
        inHandler = new Handler(inThread.getLooper());

        serviceContext = context;
    }

    //Attempt socket connection to the server
    private void connect() {
        Log.d(TAG, "Attempting connection...");
        try {
            //Create socket connected to the server
            socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(address), 2508), 3000);
            isConnected = true;
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            inHandler.post(this::listen);
            Log.d(TAG, "Successfully connected to server...");
        } catch (IOException e) {
            //Log connection failure
            isConnected = false;
            Log.d(TAG, "Failed to connect to server...\n" + e);
        }
    }

    //Send login request to server
    void login(Bundle extras) {
        if (!isConnected) connect();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = extras.getString("email");
        loginRequest.password = extras.getString("password");
        loginRequest.firebaseId = FirebaseInstanceId.getInstance().getToken();
        long lastMessageId = NetworkService.getLastMessageId();
        if (lastMessageId != 0) loginRequest.lastMessageId = lastMessageId;
        Log.d(TAG, "Attempting login: " + loginRequest.username);
        sendMessage(LOGIN_REQUEST, loginRequest);
    }

    //Send registration request to server
    void register(Bundle extras) {
        if (!isConnected) connect();
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.username = extras.getString("email");
        registrationRequest.password = extras.getString("password");
        registrationRequest.nickname = extras.getString("nickname");
        registrationRequest.firebaseId = FirebaseInstanceId.getInstance().getToken();
        sendMessage(REGISTRATION_REQUEST, registrationRequest);
    }

    //Send message with content to server [full message]
    synchronized void sendMessage(int messageType, Object message) {
        if (isConnected && output != null) try {
            output.write(messageType);
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            Log.d(TAG, "Failed to send message, disconnecting...");
            logout();
            disconnect();
            Log.d(TAG, "Starting reconnect job...");
            ReconnectJob.schedule(serviceContext);
        }
    }

    //Listen for incoming server messages
    private void listen() {
        while (isConnected) {
            try {
                //Receive message type and content
                int messageType = input.read();
                Object message = input.readObject();

                //Handle message
                execHandler.post(() -> handleMessage(messageType, message));

            } catch (IOException | ClassNotFoundException e) {
                isConnected = false;
                Log.d(TAG, "Failed to read message from server, disconnecting...");
                logout();
                disconnect();
                Log.d(TAG, "Starting reconnect job...");
                ReconnectJob.schedule(serviceContext);
            }
        }
    }

    //Process received message
    private void handleMessage(int messageType, Object message) {
        Log.d(TAG, "Received message...");
        switch (messageType) {
            case LOGIN_SUCCESS:
                Log.d(TAG, "Login success!");
                isLoggedIn = true;
                ServerResponse response = (ServerResponse) message;
                NetworkService.username = response.nickname;
                NetworkService.broadcastManager.sendBroadcast(new Intent(LOGIN_ACK));
                break;

            case LOGIN_ERROR:
                Log.d(TAG, "Login error!");
                isLoggedIn = false;
                NetworkService.database.edit().putBoolean("verified", false).apply();
                NetworkService.broadcastManager.sendBroadcast(new Intent(LOGIN_ERR));
                break;

            case TEXT_MESSAGE:
                Log.d(TAG, "Received message!");
                execHandler.post(() -> processTextMessage((Message) message));
                break;
        }
    }

    private void processTextMessage(Message message) {
        //Create data set from received message
        ContentValues values = new ContentValues();
        values.put("MESSAGE_SERVER_ID", message.messageId);
        values.put("SENDER_ID", message.senderId);
        values.put("SENDER_NICK", message.senderNick);
        values.put("TARGET_ID", 88);
        values.put("TIMESTAMP", message.timestamp);
        values.put("CONTENT", message.text);
        Log.d(TAG, "Received message ID: " + message.messageId);

        //Insert new data into SQLite Table
        NetworkService.sqliteDatabase.insert("MESSAGES", null, values);

        //Broadcast new message signal
        NetworkService.broadcastManager.sendBroadcast(new Intent(NEW_MESSAGE).putExtra("messageObject", message));
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
