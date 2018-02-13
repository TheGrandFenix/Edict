package com.fenix.edict.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fenix.edict.R;


public class EdictActivity extends Activity {
    public static final String TAG = "EDICT_ACT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edict);
    }
}
