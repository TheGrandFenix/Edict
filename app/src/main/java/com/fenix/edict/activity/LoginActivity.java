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

import static com.fenix.edict.service.NetworkService.*;

public class LoginActivity extends Activity {
    private static final String TAG = "LOGIN_ACT";

    public static final String LOGIN_ACK = "login_ack";
    public static final String LOGIN_ERR = "login_err";

    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Define layout elements
        emailInput = findViewById(R.id.email_et);
        passwordInput = findViewById(R.id.password_et);
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
        //
    }

    //Define service broadcast actions
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) switch (intent.getAction()) {
                case LOGIN_ACK:
                    startActivity(new Intent(getApplicationContext(), EdictActivity.class));
                    finish();
                    break;

                case LOGIN_ERR:
                    Toast.makeText(context, "Error on login!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
