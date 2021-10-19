package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.ui.fragment.settlement.DeviceCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.ICardReaderResult;
import com.varanegar.vaslibrary.ui.fragment.settlement.IOnActivityResultListener;
import com.varanegar.vaslibrary.ui.fragment.settlement.TransactionData;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.TELECOM_SERVICE;

public class SepehrCardReader extends DeviceCardReader implements IOnActivityResultListener {
    private VasActivity activity;
    private TransactionData td;
    private ICardReaderResult result;
    private Currency currencyAmount = Currency.ZERO;
    public static int requestCode = 912;

    public SepehrCardReader(VasActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void runTransaction(@NonNull TransactionData td, @Nullable ICardReaderResult result) {
        try {
            this.td = td;
            this.result = result;
            Intent intent = new Intent("com.dml.sima7.sepehr.activity.Intent_SwipeCardActivity");
            intent.putExtra("amount", td.Amount.replaceAll(",", ""));
            currencyAmount = Currency.parse(td.Amount.replaceAll(",", ""));
//            intent.putExtra("isPrint", td.Amount);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP/* | Intent.FLAG_ACTIVITY_NEW_TASK*/);
            activity.sepehrCardReader = this;
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Timber.e("Main", "Second application is not installed!");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onReceiveResult(Context context, int requestCode, int resultCode, @Nullable Intent data, @Nullable Bundle bundle) {
        Timber.e("IranKish result recieved.");
        if (result != null && td != null && data != null) {
            if (data.getExtras() != null) {
                Bundle bundle1 = data.getExtras();
                if (requestCode == 912 && resultCode == RESULT_OK) {
//                    String paymentAmount = bundle1.getString("amount");
                    String referenceCode = bundle1.getString("refrenceCode");
                    if(bundle1.getString("resultCode").equals("00")) {
                        try {
                            td.PaidAmount = currencyAmount;
                            td.TransactionNo = referenceCode;
                            td.PaymentTime = new Date();
                            result.onSuccess(td);
                        } catch (Exception e) {
//                        Timber.e("Error In IranKishCardReader \n" + e);
//                        td.setPaymentFailure(referenceCode, null, getContext().getString(R.string.transaction_amount_is_not_valid));
//                        result.onFailure(td, td.ErrorString);
                        }
                    } else {
                        Timber.e(bundle1.getString("resultDescription"));
                        td.setPaymentFailure("", Currency.ZERO, bundle1.getString("resultDescription"));
                    }

                } else {
                    String msg = bundle1.getString("message");
                    if (msg != null && !msg.equals("")) {
                        Timber.e("Error In SepehrReader \n" + msg);
                        td.setPaymentFailure("", Currency.ZERO, msg);
                    } else {
                        Timber.e("Error In SepehrCardReader, result not success");
                        td.setPaymentFailure("", Currency.ZERO, activity.getString(R.string.error_in_payment_system));
                    }
                    result.onFailure(td, td.ErrorString);
                }
            } else {
                Timber.e("SepehrCardReader data doesn't have extras.");
                td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_payment_system));
                result.onFailure(td, td.ErrorString);
            }
//            Toast.makeText(activity, data.getStringExtra("amount"), Toast.LENGTH_LONG).show();
//            Toast.makeText(activity, data.getStringExtra("refrenceCode"), Toast.LENGTH_LONG).show();
//            Toast.makeText(activity, data.getStringExtra("resultCode"), Toast.LENGTH_LONG).show();
        }
    }
}
