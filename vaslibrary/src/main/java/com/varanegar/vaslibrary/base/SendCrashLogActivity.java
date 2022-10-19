package com.varanegar.vaslibrary.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.logging.LogConfig;
import com.varanegar.vaslibrary.R;

import timber.log.Timber;

/**
 * Created by m-latifi on 10/19/2022.
 */

public class SendCrashLogActivity extends MainVaranegarActivity {

    private TextView textViewWaiting;
    private Button buttonClose;

    //---------------------------------------------------------------------------------------------- onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_log);
        String error = getIntent().getExtras().getString("data");
        Timber.e(error);
        buttonClose = findViewById(R.id.buttonClose);
        textViewWaiting = findViewById(R.id.textViewWaiting);
        buttonClose.setVisibility(View.GONE);
        textViewWaiting.setVisibility(View.VISIBLE);
        buttonClose.setOnClickListener(v -> System.exit(1));
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- checkStoragePermission
    @Override
    protected boolean checkStoragePermission() {
        return false;
    }
    //---------------------------------------------------------------------------------------------- checkStoragePermission


    //---------------------------------------------------------------------------------------------- checkCameraPermission
    @Override
    protected boolean checkCameraPermission() {
        return false;
    }
    //---------------------------------------------------------------------------------------------- checkCameraPermission


    //---------------------------------------------------------------------------------------------- checkLocationPermission
    @Override
    protected boolean checkLocationPermission() {
        return false;
    }
    //---------------------------------------------------------------------------------------------- checkLocationPermission


    //---------------------------------------------------------------------------------------------- createLogConfig
    @Override
    protected LogConfig createLogConfig() {
        return null;
    }
    //---------------------------------------------------------------------------------------------- createLogConfig
}
