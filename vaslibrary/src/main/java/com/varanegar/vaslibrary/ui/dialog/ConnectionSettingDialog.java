package com.varanegar.vaslibrary.ui.dialog;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.vaslibrary.R;

import static android.net.wifi.WifiManager.ACTION_PICK_WIFI_NETWORK;
import static android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS;

/**
 * Created by A.Torabi on 8/22/2017.
 */

public class ConnectionSettingDialog extends CuteDialog {
    public ConnectionSettingDialog(){
        setSizingPolicy(SizingPolicy.Medium);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_connection_settings, container, false);
        view.findViewById(R.id.wifi_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                startActivity(new Intent(ACTION_PICK_WIFI_NETWORK));
            }
        });
        view.findViewById(R.id.data_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                startActivity(new Intent(ACTION_DATA_ROAMING_SETTINGS));
            }
        });
        return view;
    }

}
