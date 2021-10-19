package varanegar.com.vdmclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import varanegar.com.vdmclient.handlers.VdmHandler;

/**
 * Created by A.Torabi on 7/16/2018.
 */

public class VdmClient {
    private static final String PACKAGE_NAME = "com.varanegar.vdm";
    private final Context context;
    private final String appId;
    private boolean isConnected;
    private Messenger boundServiceMessenger;

    public VdmClient(Context context, String appId) {
        this.context = context;
        this.appId = appId;
    }

    public boolean isPackageInstalled() {
        boolean found = true;
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    public void bind(@NonNull final OnBind onBind) {
        Intent intent = new Intent("com.varanegar.vdmservice");
        intent.setPackage(PACKAGE_NAME);
        ServiceConnection connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                boundServiceMessenger = new Messenger(iBinder);
                isConnected = true;
                onBind.connected();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                boundServiceMessenger = null;
                isConnected = false;
                onBind.disConnected();
            }
        };
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void run(VdmHandler handler) throws RemoteException, NoConnectionException {
        Message msg = Message.obtain(null, handler.getId());
        msg.setData(handler.getBundle());
        msg.replyTo = new Messenger(handler);
        if (isConnected)
            boundServiceMessenger.send(msg);
        else
            throw new NoConnectionException();
    }

    public String getAppId() {
        return appId;
    }

    public Context getContext() {
        return context;
    }

    @Nullable
    public String getSessionId() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VdmClient", Context.MODE_PRIVATE);
        return sharedPreferences.getString("SessionId", null);
    }

    protected void saveSessionId(String sessionId) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VdmClient", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("SessionId", sessionId).apply();
    }
}
