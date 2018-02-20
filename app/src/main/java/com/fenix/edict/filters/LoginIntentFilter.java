package com.fenix.edict.filters;

import android.content.IntentFilter;

import static com.fenix.edict.activity.LoginActivity.*;


public class LoginIntentFilter extends IntentFilter {
    public LoginIntentFilter() {
        addAction(LOGIN_ACK);
        addAction(LOGIN_ERR);
    }
}
