package com.varanegar.vaslibrary.ui.fragment.vpnfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.util.vpn_openvpn.CheckInternetConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

public class VpnDialogFragment extends CuteDialogWithToolbar {
    private View view;
    boolean vpnStart = false;
    private CheckInternetConnection connection;
    private Button coonect_vpn;
    private EditText user_name_vpn;
    private EditText password_vpn;
    private String usernameVpn,  passwordVpn;
    private SharedPreferences sharedconditionCustomer;
    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter("connectionState"));

        super.onResume();
    }
    /**
     * Internet connection status.
     */
    public boolean getInternetStatus() {
        return connection.netCheck(getContext());
    }
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vpn_dialog, container, false);
        connection = new CheckInternetConnection();
        coonect_vpn=view.findViewById(R.id.coonect_vpn);
        sharedconditionCustomer = getActivity().getSharedPreferences("OpenVPN",
                Context.MODE_PRIVATE);
        usernameVpn=sharedconditionCustomer.getString("usernameVpn","");
        passwordVpn=sharedconditionCustomer.getString("passwordVpn","");

        user_name_vpn=view.findViewById(R.id.user_name_vpn);
        password_vpn=view.findViewById(R.id.password_vpn);

        if (!usernameVpn.isEmpty() && usernameVpn!=null  &&!passwordVpn.isEmpty() &&
                passwordVpn!=null ) {
            user_name_vpn.setText(usernameVpn);
            password_vpn.setText(passwordVpn);
        }
        coonect_vpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameVpn=user_name_vpn.getText().toString();
                passwordVpn=password_vpn.getText().toString();
                if (vpnStart) {
                    confirmDisconnect();
                }else {
                    if (!usernameVpn.isEmpty() && usernameVpn!=null  &&!passwordVpn.isEmpty() &&
                            passwordVpn!=null ) {
                        prepareVpn();
                    }else {
                        showToast("لطفا نام کاربری و پسورد را وارد کنید");
                    }
                }
            }
        });
        isServiceRunning();
        VpnStatus.initLogCache(getActivity().getCacheDir());
        return view;
    }
    /**
     * Show show disconnect confirm dialog
     */
    public void confirmDisconnect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("قطع کردن اتصال وی پی ان");

        builder.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopVpn();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * Start the VPN
     */
    private void startVpn() {
        try {
            // .ovpn file
            InputStream conf = getActivity().getAssets().open("zarpakhsh.ovpn");
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;

            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }
            sharedconditionCustomer.edit().putString("usernameVpn",usernameVpn).apply();
            sharedconditionCustomer.edit().putString("passwordVpn",passwordVpn).apply();
            br.readLine();
            OpenVpnApi.startVpn(getContext(), config, "زرماکارون", usernameVpn, passwordVpn);

            // Update log

            vpnStart = true;

        } catch (IOException | RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * Show toast message
     * @param message: toast message
     */
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Prepare for vpn connect with required permission
     */
    private void prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {

                // Checking permission for network monitor
                Intent intent = VpnService.prepare(getContext());

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();//have already permission

                // Update confection status
                status("connecting");

            } else {

                // No internet connection available
                showToast("you have no internet connection !!");
            }

        } else if (stopVpn()) {

            // VPN is stopped, show a Toast message.
            showToast("Disconnect Successfully");
        }
    }


    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    public boolean stopVpn() {
        try {
            vpnThread.stop();

            status("connect");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get service status
     */
    public void isServiceRunning() {
        setStatus(vpnService.getStatus());
    }
    /**
     * Receive broadcast message
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";
                // updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    public void setStatus(String connectionState) {
        if (connectionState!= null)
            switch (connectionState) {
                case "DISCONNECTED":
                    status("connect");
                    vpnStart = false;

                    break;
                case "CONNECTED":
                    vpnStart = true;// it will use after restart this activity
                    status("connected");

                    break;
                case "WAIT":

                    break;
                case "AUTH":

                    break;
                case "RECONNECTING":
                    status("connecting");

                    break;
                case "NONETWORK":

                    break;
            }

    }

    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    public void status(String status) {

        if (status.equals("connect")) {
            coonect_vpn.setText("اتصال");
            coonect_vpn.setBackgroundColor(Objects.requireNonNull(getActivity())
                    .getResources().getColor(R.color.grey));
            coonect_vpn.setTextColor(getActivity().getResources().getColor(R.color.black));
        } else if (status.equals("connecting")) {
            coonect_vpn.setText("درحال اتصال");
            coonect_vpn.setBackgroundColor(Objects.requireNonNull(getActivity())
                    .getResources().getColor(R.color.red));
            coonect_vpn.setTextColor(getActivity().getResources().getColor(R.color.white));
        } else if (status.equals("connected")) {
            coonect_vpn.setText("متصل شد");
            coonect_vpn.setBackgroundColor(Objects.requireNonNull(getActivity())
                    .getResources().getColor(R.color.green));
            coonect_vpn.setTextColor(getActivity().getResources().getColor(R.color.white));
        } else if (status.equals("tryDifferentServer")) {


        } else if (status.equals("loading")) {
            coonect_vpn.setText("Loading Server..");
        } else if (status.equals("invalidDevice")) {
            coonect_vpn.setText("Invalid Device");
        } else if (status.equals("authenticationCheck")) {

        }
    }
}
