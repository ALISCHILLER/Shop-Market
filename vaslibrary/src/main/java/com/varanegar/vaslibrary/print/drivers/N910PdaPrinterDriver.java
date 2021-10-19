package com.varanegar.vaslibrary.print.drivers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;

import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.N910CardReader;

import ir.sep.android.Service.IProxy;
import timber.log.Timber;

/**
 * Created by e.hashemzadeh on 6/1/2021.
 */
public class N910PdaPrinterDriver extends PrinterDriver {
    Bitmap bitmap;
    PrintCallback printCallback;
    public IProxy service;
    MyServiceConnection connection ;

    public N910PdaPrinterDriver(Context context) {
        super(context);
    }

    @Override
    public int printWidth() {
        return 58;
    }

    @Override
    public String name() {
        return "N910";
    }

    @Override
    public int dpi() {
        return 172;
    }

    @Override
    public void print(Bitmap bitmap, PrintCallback printCallback) {
        this.bitmap = bitmap;
        this.printCallback = printCallback;
        initService();
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) {
        connectionCallback.connected();
    }

    @Override
    public void disconnect() {
        releaseService();
    }

    private void initService() {
        Timber.i("initService()");
        connection = new MyServiceConnection();
        Intent i = new Intent();
        i.setClassName("ir.sep.android.smartpos", "ir.sep.android.Service.Proxy");
        boolean ret = context.bindService(i, connection, Context.BIND_AUTO_CREATE);
        Timber.i("initService() bound value: " + ret);
    }

    private void releaseService() {
        if (connection != null) {
           context.unbindService(connection);
            connection = null;
        }
        Timber.i("releaseService(): unbound.");
    }

    class MyServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            try {
                service = IProxy.Stub.asInterface((IBinder) boundService);
                Timber.i("onServiceConnected(): Connected");
                service.PrintByBitmap(bitmap);
                printCallback.done();
            } catch (RemoteException e) {
                Timber.e(e);
                printCallback.failed();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Timber.i("onServiceDisconnected(): Disconnected");
        }
    }
}
