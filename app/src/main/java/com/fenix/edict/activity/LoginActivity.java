package com.fenix.edict.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.fenix.edict.R;
import com.fenix.edict.service.NetworkService;

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

    public void onLogin(View view) {
        String email = String.valueOf(emailInput.getText());
        String password = String.valueOf(passwordInput.getText());
        if (!email.equals("") && !password.equals("")) {
            Bundle extras = new Bundle();
            extras.putString("email", email);
            extras.putString("password", password);

            Intent intent = new Intent();
            intent.setClass(this, NetworkService.class);
            intent.putExtras(extras);
            startService(intent);

        } else {
            //Handle invalid login
        }
    }

    public void onSignup(View view) {
        String email = String.valueOf(emailInput.getText());
        String password = String.valueOf(passwordInput.getText());
        if (!email.equals("") && !password.equals("")) {
            Bundle extras = new Bundle();
            extras.putString("email", email);
            extras.putString("password", password);
        } else {
            //Handle invalid login
        }
    }
}
