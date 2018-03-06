package com.fenix.edict.filters;

import android.content.IntentFilter;

import static com.fenix.edict.activity.EdictActivity.NEW_MESSAGE;

public class EdictIntentFilter extends IntentFilter {
    public EdictIntentFilter() {
        addAction(NEW_MESSAGE);
    }
}
