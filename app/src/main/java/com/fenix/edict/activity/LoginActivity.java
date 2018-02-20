package com.fenix.edict.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fenix.edict.R;
import com.fenix.edict.filters.LoginIntentFilter;
import com.fenix.edict.service.Connection;
import com.fenix.edict.service.NetworkService;

import static com.fenix.edict.service.NetworkService.*;

public class LoginActivity extends Activity {
    private static final String TAG = "LOGIN_ACT";

    public static final String LOGIN_ACK = "login_ack";
    public static final String LOGIN_ERR = "login_err";

    private EditText emailInput;
    private EditText passwordInput;

    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Define layout elements
        emailInput = findViewById(R.id.email_et);
        passwordInput = findViewById(R.id.password_et);

        //Get broadcast manager
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    //Restart service with login intent - on button click [REPLACE WITH BROADCAST]
    public void onLogin(View view) {
        //Get credentials from input fields
        String email = String.valueOf(emailInput.getText());
        String password = String.valueOf(passwordInput.getText());

        if (!email.equals("") && !password.equals("")) {
            //Create data bundle with email and password
            Bundle extras = new Bundle();
            extras.putString("email", email);
            extras.putString("password", password);

            //Request login - broadcast to NetworkService
            Intent intent = new Intent(LOGIN);
            intent.putExtras(extras);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            //Start loading visual layout
            setContentView(R.layout.activity_login_loading);
        }
    }

    //Restart service with registration intent - on button click
    public void onSignup(View view) {
        //Get credentials from input fields
        String email = String.valueOf(emailInput.getText());
        String password = String.valueOf(passwordInput.getText());

        if (!email.equals("") && !password.equals("")) {
            //Create data bundle with email and password
            Bundle extras = new Bundle();
            extras.putString("email", email);
            extras.putString("password", password);

            //Request login - broadcast to NetworkService
            Intent intent = new Intent(REGISTER);
            intent.putExtras(extras);
            broadcastManager.sendBroadcast(intent);

            //Start loading visual layout
            setContentView(R.layout.activity_login_loading);
        }
    }

    //Define service broadcast actions
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) switch (intent.getAction()) {
                //Proceed to EdictActivity if login was successful
                case LOGIN_ACK:
                    startActivity(new Intent(getApplicationContext(), EdictActivity.class));
                    finish();
                    break;

                //Display error message if login failed
                case LOGIN_ERR:
                    Toast.makeText(context, "Error on login!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    //Register receiver when app is resumed
    @Override
    protected void onResume() {
        super.onResume();
        broadcastManager.registerReceiver(broadcastReceiver, new LoginIntentFilter());

        //Proceed to EdictActivity if successful login occurred while layout was paused
        if (NetworkService.connection.isLoggedIn) {
            startActivity(new Intent(getApplicationContext(), EdictActivity.class));
            finish();
        }
    }

    //Unregister receiver when app is paused
    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
