package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.broadcasts.TEPBroadCast;
import com.varanegar.vaslibrary.broadcasts.TEPResponse;
import com.varanegar.vaslibrary.ui.fragment.settlement.DeviceCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.ICardReaderResult;
import com.varanegar.vaslibrary.ui.fragment.settlement.IOnActivityResultListener;
import com.varanegar.vaslibrary.ui.fragment.settlement.TransactionData;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by saeedeh on 4/12/2021.
 */
public class TejaratElectronicParsianCardReader extends DeviceCardReader implements IOnActivityResultListener {
    private VasActivity activity;
    private TransactionData td;
    private ICardReaderResult result;

    public TejaratElectronicParsianCardReader(VasActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void runTransaction(@NonNull TransactionData td, @Nullable ICardReaderResult result) {
        this.td = td;
        this.result = result;
        Intent i = new Intent("com.pec.smartpos.cpsdk");
        i.putExtra("transactionType", "Sale");
        i.putExtra("AM", HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount));
        i.putExtra("CompanyName", "Varanegar");
        activity.tejaratElectronicParsianCardReaderListener = this;
        activity.startActivity(i);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onReceiveResult(Context context, int requestCode, int resultCode, @Nullable Intent data, @Nullable Bundle bundle) {
        Timber.e("TejaratElectronicParsian result recieved.");
        if (result != null && td != null && data != null)
            if (data.getExtras() != null) {
                String message = data.getStringExtra(TEPResponse.Error);
                if (data.getStringExtra(TEPResponse.ResponseCode).equals("00")) {
                    try {
                        Timber.e("success payment In TejaratElectronicParsianCardReader \n" + " response code: " + data.getStringExtra(TEPResponse.ResponseCode) + " serial number: " + data.getStringExtra(TEPResponse.SerialNumber));
                        td.PaidAmount = Currency.parse(td.Amount);
                        td.TransactionNo = data.getStringExtra(TEPResponse.SerialNumber);
                        td.PaymentTime = new Date();
                        result.onSuccess(td);
                    } catch (ParseException e) {
                        Timber.e("Error In TejaratElectronicParsianCardReader \n" + e +" "+ message+ " response code: " + data.getStringExtra(TEPResponse.ResponseCode) + " serial number: " + data.getStringExtra(TEPResponse.SerialNumber));
                        td.setPaymentFailure(data.getStringExtra(TEPResponse.RRN), null, getContext().getString(R.string.transaction_amount_is_not_valid));
                        result.onFailure(td, td.ErrorString);
                    }
                } else {

                    if (message != null) {
                        Timber.e("Error In TejaratElectronicParsianCardReader \n" + message + " response code: " + data.getStringExtra(TEPResponse.ResponseCode) + " serial number: " + data.getStringExtra(TEPResponse.SerialNumber));
                        td.setPaymentFailure("", Currency.ZERO, message);
                        result.onFailure(td, td.ErrorString);
                    } else {
                        Timber.e("Error In TejaratElectronicParsianCardReader \n" + " تراکنش ناموفق " + " response code: " + data.getStringExtra(TEPResponse.ResponseCode) + " serial number: " + data.getStringExtra(TEPResponse.SerialNumber));
                        td.setPaymentFailure("", Currency.ZERO, "تراکنش ناموفق");
                        result.onFailure(td, td.ErrorString);
                    }
                }
                Intent intent = new Intent();
                ComponentName cName = new ComponentName("com.pec.smartpos", "com.pec.smartpos.cpsdk.PecService");
                intent.setComponent(cName);
                intent.putExtra("printType", "receiptSale");
                intent.putExtra("RRN", data.getStringExtra(TEPResponse.RRN));
                intent.putExtra("respCode", data.getStringExtra(TEPResponse.ResponseCode));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.startForegroundService(intent);
                } else {
                    activity.startService(intent);
                }
            }
    }
}
