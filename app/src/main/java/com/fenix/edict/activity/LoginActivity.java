package com.fenix.edict.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;

import com.fenix.edict.R;

import static com.fenix.edict.service.NetworkService.*;

public class LoginActivity extends Activity {
    public static final String TAG = "LOGIN_ACT";

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
}
