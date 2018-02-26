package com.fenix.edict.filters;

import android.content.IntentFilter;
import android.net.ConnectivityManager;

import static com.fenix.edict.service.NetworkService.*;

public class ServiceIntentFilter extends IntentFilter {
    public ServiceIntentFilter() {
        addAction(LOGIN);
        addAction(REGISTER);
        addAction(LOGOUT);
        addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }
}
