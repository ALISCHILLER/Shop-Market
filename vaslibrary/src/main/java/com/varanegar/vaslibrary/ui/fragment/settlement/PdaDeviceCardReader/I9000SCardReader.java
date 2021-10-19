package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Elnaz on 6/6/2020.
 */

public class I9000SCardReader extends DeviceCardReader implements IOnActivityResultListener {
    private final VasActivity activity;
    public static int i9000S_requestCode;
    private TransactionData td;
    private ICardReaderResult result;

    public I9000SCardReader(VasActivity context) {
        super(context);
        this.activity = context;
    }

    @Override
    public void runTransaction(@NonNull TransactionData td, @Nullable ICardReaderResult result) {
        try {
            if (installedOrNot(activity, "ir.totan.pos")) { // Totan
                this.td = td;
                this.result = result;
                i9000S_requestCode = 103;
                int mType = 3;
                String mAmount = HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount);
                Intent intent = new Intent("ir.totan.pos.view.cart.TXN");
                intent.putExtra("type", mType);
                if (mAmount != null)
                    intent.putExtra("amount", mAmount);
                intent.putExtra("res_num", "11 ");
                activity.i9000sCardReaderListener = this;
                activity.startActivityForResult(intent, 103);
            } else { //PardakhtNovin
                this.td = td;
                this.result = result;
                i9000S_requestCode = 1002;
                Intent intent = new Intent("ir.co.pna.pos.view.cart.IAPCActivity");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("AndroidPosMessageHeader", "@@PNA@@");
                jsonObject.put("ECRType", "1");
                jsonObject.put("Amount", HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount));
                jsonObject.put("TransactionType", "00");
                Bundle bundle = new Bundle();
                bundle.putString("Data", jsonObject.toString());
                intent.putExtras(bundle);
                activity.i9000sCardReaderListener = this;
                activity.startActivityForResult(intent, i9000S_requestCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveResult(Context context, int requestCode, int resultCode, @Nullable Intent data, @Nullable Bundle bundle) {
        Timber.e("i9000s result recieved.");
        if (result != null && td != null && data != null) {
            if (installedOrNot(activity, "ir.totan.pos")) { // Totan
                Bundle b = data.getBundleExtra("response");
                if (resultCode == Activity.RESULT_OK && b != null) {
                    if (b.getString("result").equals("succeed")) {
                        try {
                            td.PaidAmount = Currency.parse(b.getString("amount", td.Amount));
                            td.TransactionNo = b.getString("trace", "");
                            td.PaymentTime = new Date();
                            result.onSuccess(td);
                        } catch (ParseException e) {
                            td.setPaymentFailure(b.getString("trace", ""), null, getContext().getString(R.string.transaction_amount_is_not_valid));
                            result.onFailure(td, td.ErrorString);
                        }
                    } else {
                        td.setPaymentFailure(b.getString("trace", ""), Currency.ZERO, b.getString("message", activity.getString(R.string.error)));
                        result.onFailure(td, td.ErrorString);
                    }
                } else {
                    Timber.e("Totan data doesn't have the Result. ResuldCode is " + resultCode);
                    td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error));
                    result.onFailure(td, td.ErrorString);
                }
            } else { //PardakhtNovin
                if (data.hasExtra("Result")) {
                    try {
                        String jsonString = data.getStringExtra("Result");
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String Status = jsonObject.getString("Status");
                        if (Status.equals("OK")) {
                            String STAN = jsonObject.getString("STAN");
                            String ResponseCode = jsonObject.getString("ResponseCode");
                            if (ResponseCode.equals("00")) {
                                try {
                                    td.PaidAmount = Currency.parse(td.Amount);
                                    td.TransactionNo = STAN;
                                    td.PaymentTime = new Date();
                                    result.onSuccess(td);
                                } catch (ParseException e) {
                                    td.setPaymentFailure(STAN, null, getContext().getString(R.string.transaction_amount_is_not_valid));
                                    result.onFailure(td, td.ErrorString);
                                }
                            } else {
                                td.setPaymentFailure(STAN, Currency.ZERO, jsonObject.getString("Description"));
                                result.onFailure(td, td.ErrorString);
                            }

                        } else {
                            td.setPaymentFailure("", Currency.ZERO, jsonObject.getString("Description"));
                            result.onFailure(td, td.ErrorString);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        td.setPaymentFailure("", Currency.ZERO, getContext().getString(R.string.error));
                        result.onFailure(td, td.ErrorString);
                    }
                } else {
                    Timber.e("PardakhtNovin data doesn't have the Result key.");
                    td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_payment_system));
                    result.onFailure(td, td.ErrorString);
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (activity != null)
            activity.i9000sCardReaderListener = null;
    }

    private boolean installedOrNot(Context cnt, String packageName) {

        PackageManager pm = cnt.getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;

    }
}
