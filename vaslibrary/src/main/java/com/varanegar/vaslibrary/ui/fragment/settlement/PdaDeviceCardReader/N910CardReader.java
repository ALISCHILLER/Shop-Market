package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.ui.fragment.settlement.DeviceCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.ICardReaderResult;
import com.varanegar.vaslibrary.ui.fragment.settlement.IOnActivityResultListener;
import com.varanegar.vaslibrary.ui.fragment.settlement.TransactionData;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import ir.sep.android.Service.IProxy;
import timber.log.Timber;

/**
 * Created by e.hashemzadeh on 6/1/2021.
 */
public class N910CardReader extends DeviceCardReader implements IOnActivityResultListener { //SamanKish
    private VasActivity activity;
    private TransactionData td;
    private ICardReaderResult result;
    public static int requestCode = 1;
    MyServiceConnection connection;
    MyServiceConnectionAndVerify connectionAndVerify;
    IProxy service;

    public N910CardReader(VasActivity activity) {
        super(activity);
        this.activity = activity;
        initService();
    }

    @Override
    public void runTransaction(@NonNull TransactionData td, @Nullable ICardReaderResult result) {
        this.td = td;
        this.result = result;
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        Intent intent = new Intent();
        intent.putExtra("TransType", 1);
        intent.putExtra("Amount", HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount));
        intent.putExtra("ResNum", randomUUIDString);
        intent.putExtra("AppId", "1");
        intent.putExtra("Timer", 60_000);
        intent.setComponent(new ComponentName("ir.sep.android.smartpos", "ir.sep.android.smartpos.ThirdPartyActivity"));
        activity.n910CardReader = this;
        activity.startActivityForResult(intent, 1);
    }

    @Override
    public void dispose() {
        if (activity != null)
            activity.n910CardReader = null;
        releaseService();
    }

    @Override
    public void onReceiveResult(Context context, int requestCode, int resultCode, @Nullable Intent data, @Nullable Bundle bundle) {
        Timber.e("N910 result recieved.");
        if (result != null && td != null && data != null) {
            int state = data.getIntExtra("State", -1);
            String msg = "";
            if (!TextUtils.isEmpty(data.getStringExtra("result")))
                msg = data.getStringExtra("result");
            if (resultCode == Activity.RESULT_OK) {
                if (state == 0) {
                    verifyTransaction(data);
                } else {
                    Timber.e("N910 transaction failed");
                    td.setPaymentFailure("", Currency.ZERO, msg);
                    result.onFailure(td, td.ErrorString);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.e("N910 transaction canceled");
                td.setPaymentFailure("", Currency.ZERO, activity.getString(R.string.transaction_canceled));
                result.onFailure(td, td.ErrorString);
            } else {
                Timber.e("Error In N910, result not success. " + msg);
                td.setPaymentFailure("", Currency.ZERO, (msg.equals("") ? activity.getString(R.string.error) : msg));
                result.onFailure(td, td.ErrorString);
            }
        }
    }

    private void verifyTransaction(Intent data) {
        if (service == null) {
            initServiceAndVerify(data);
        } else {
            verify(data);
        }
    }

    private void initService() {
        Timber.i("initService()");
        connection = new MyServiceConnection();
        Intent i = new Intent();
        i.setClassName("ir.sep.android.smartpos", "ir.sep.android.Service.Proxy");
        boolean ret = activity.bindService(i, connection, Context.BIND_AUTO_CREATE);
        Timber.i("initService() bound value: " + ret);
    }

    private void releaseService() {
        if (connection != null) {
            activity.unbindService(connection);
            connection = null;
        }
        if (connectionAndVerify != null) {
            activity.unbindService(connectionAndVerify);
            connectionAndVerify = null;
        }
        Timber.i("releaseService(): unbound.");
    }

    private void initServiceAndVerify(Intent data) {
        Timber.i("initService()");
        connectionAndVerify = new MyServiceConnectionAndVerify(data);
        Intent i = new Intent();
        i.setClassName("ir.sep.android.smartpos", "ir.sep.android.Service.Proxy");
        boolean ret = activity.bindService(i, connectionAndVerify, Context.BIND_AUTO_CREATE);
        Timber.i("initService() bound value: " + ret);
    }

    class MyServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IProxy.Stub.asInterface((IBinder) boundService);
            Timber.i("onServiceConnected(): Connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Timber.i("onServiceDisconnected(): Disconnected");
        }
    }

    class MyServiceConnectionAndVerify implements ServiceConnection {
        Intent data;

        public MyServiceConnectionAndVerify(Intent data) {
            this.data = data;
        }

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IProxy.Stub.asInterface((IBinder) boundService);
            Timber.i("onServiceConnected(): Connected");
            verify(data);
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Timber.i("onServiceDisconnected(): Disconnected");
        }
    }

    public void verify(Intent data) {
        int state = data.getIntExtra("State", -1);
        String refNum = data.getStringExtra("RefNum");
        String resNum = data.getStringExtra("ResNum");
        String traceNumber = data.getStringExtra("TraceNumber");
        String amount = data.getStringExtra("Amount");
        String terminalId = data.getStringExtra("TerminalId");
        String cardNumber = data.getStringExtra("Pan");
        String dateTime = data.getStringExtra("DateTime");
        String amountAffective = data.getStringExtra("AmountAffective");
        String additionalData = data.getStringExtra("AdditionalData");
        try {
            int verifyResult = service.VerifyTransaction(1, refNum, resNum);
            if (verifyResult == 0 || verifyResult == 1) { // success
                try {
                    if (verifyResult == 1)
                        service.PrintByRefNum(refNum);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Timber.e(e);
                }
                try {
                    td.PaidAmount = Currency.parse(amount);
                    td.TransactionNo = traceNumber;
                    td.PaymentTime = new Date();
                    result.onSuccess(td);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Timber.e(e);
                    td.setPaymentFailure(refNum, null, getContext().getString(R.string.transaction_amount_is_not_valid));
                    result.onFailure(td, td.ErrorString);
                }
            } else { // failed
                Timber.e("N910 transaction failed");
                td.setPaymentFailure("", Currency.ZERO, activity.getString(R.string.error));
                result.onFailure(td, td.ErrorString);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Timber.e(e);
            td.setPaymentFailure("", Currency.ZERO, activity.getString(R.string.error));
            result.onFailure(td, td.ErrorString);
        }
    }
}

