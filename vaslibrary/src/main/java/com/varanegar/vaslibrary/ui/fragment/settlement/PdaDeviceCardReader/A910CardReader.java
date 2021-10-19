package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.ui.fragment.settlement.DeviceCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.ICardReaderResult;
import com.varanegar.vaslibrary.ui.fragment.settlement.IOnActivityResultListener;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910Payment.BPMBaseUtils;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910Payment.BPMPaymentData;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910Payment.BPMPaymentResult;
import com.varanegar.vaslibrary.ui.fragment.settlement.TransactionData;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import ir.ikccc.externalpayment.TransactionRequest;
import ir.ikccc.externalpayment.TransactionType;
import timber.log.Timber;

/**
 * Created by Elnaz on 7/6/2020.
 */

public class A910CardReader extends DeviceCardReader implements IOnActivityResultListener {

    private VasActivity activity;
    private TransactionData td;
    private ICardReaderResult result;

    public A910CardReader(VasActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void runTransaction(@NonNull TransactionData td, @Nullable ICardReaderResult result) {
        if (installedOrNot(activity, "com.pos.pax")) {//check if it is beh pradakht PDA or not! packageName(debug):"com.bpm.pos"  and  packageName(signed PDA):"com.pos.pax"
            this.td = td;
            this.result = result;
            BPMPaymentData request = new BPMPaymentData();
            request.accountId = null;
            byte[] randData = new byte[8];
            new Random().nextBytes(randData);
            request.setSessionId(BPMBaseUtils.byteArr2HexStr(randData));
            request.setVersionName("1.0.0");
            request.setApplicationId(10036);
            request.setTotalAmount(Long.valueOf(HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount)));
            request.setTransactionType(BPMPaymentData.TransactionType.PURCHASE);
            request.setCardHolderMobile("");
            Intent intent = new Intent("com.bpmellat.merchant");
            intent.putExtra("PaymentData", new Gson().toJson(request));
            activity.a910CardReaderListener = this;
            activity.startActivityForResult(intent, 1010);

        } else { //IranKish
            this.td = td;
            this.result = result;
            TransactionRequest transactionRequest = new TransactionRequest(activity);
            transactionRequest.setRequestType(TransactionType.PURCHASE);
            transactionRequest.setAmount(HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount));
            transactionRequest.setPrint(true);
            if (!transactionRequest.send())
                result.onFailure(td, activity.getString(R.string.error));
            else
                activity.a910CardReaderListener = this;
        }
    }

    @Override
    public void dispose() {
        if (activity != null)
            activity.a910CardReaderListener = null;
    }

    @Override
    public void onReceiveResult(Context context, int requestCode, int resultCode, @Nullable Intent data, @Nullable Bundle bundle) {
        if (installedOrNot(activity, "com.pos.pax")) {//check if it is beh pradakhte mellat PDA or not! packageName(debug):"com.bpm.pos"  and  packageName(signed PDA):"com.pos.pax"
            Timber.e("BehPardakhteMellat result recieved.");
            if (result != null && td != null && data != null)
                if (data.getExtras() != null) {
                    String jsonStr = data.getStringExtra("PaymentResult");
                    Gson gson = new Gson();
                    BPMPaymentResult paymentResult = gson.fromJson(jsonStr, BPMPaymentResult.class);
                    if (resultCode == -1 && paymentResult.getResultCode() == 0) {
                        String paymentAmount = String.valueOf(paymentResult.getTransactionAmount());
                        String refCode= String.valueOf(paymentResult.getReferenceID());
                        String referenceCode = refCode.substring(refCode.length()-6);
                        try {
                            td.PaidAmount = Currency.parse(paymentAmount);
                            td.TransactionNo = referenceCode;
                            td.PaymentTime = new Date();
                            result.onSuccess(td);
                        } catch (ParseException e) {
                            Timber.e("Error In BehPardakhteMellatCardReader \n" + e);
                            td.setPaymentFailure(referenceCode, null, getContext().getString(R.string.transaction_amount_is_not_valid));
                            result.onFailure(td, td.ErrorString);
                        }
                    } else {
                        String msg = paymentResult.getAcquirerMessage();
                        if (msg != null && !msg.equals("")) {
                            Timber.e("Error In BehPardakhteMellatCardReader \n" + msg);
                            td.setPaymentFailure("", Currency.ZERO, msg);
                            result.onFailure(td, td.ErrorString);
                        } else {
                            Timber.e("Error In BehPardakhteMellatCardReader, result not success");
                            td.setPaymentFailure("", Currency.ZERO, activity.getString(R.string.error_in_payment_system));
                            result.onFailure(td, td.ErrorString);
                        }
                    }

                } else {
                    Timber.e("BehPardakhteMellatCardReader data doesn't have extras.");
                    td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_payment_system));
                    result.onFailure(td, td.ErrorString);
                }

        } else {
            Timber.e("IranKish result recieved.");
            if (result != null && td != null && data != null)
                if (data.getExtras() != null) {
                    Bundle bundle1 = data.getExtras();
                    if (resultCode == 0) {
                        String paymentAmount = bundle1.getString("paymentAmount");
                        String referenceCode = bundle1.getString("referenceCode");
                        try {
                            td.PaidAmount = Currency.parse(paymentAmount);
                            td.TransactionNo = referenceCode;
                            td.PaymentTime = new Date();
                            result.onSuccess(td);
                        } catch (ParseException e) {
                            Timber.e("Error In IranKishCardReader \n" + e);
                            td.setPaymentFailure(referenceCode, null, getContext().getString(R.string.transaction_amount_is_not_valid));
                            result.onFailure(td, td.ErrorString);
                        }
                    } else {
                        String msg = bundle1.getString("message");
                        if (msg != null && !msg.equals("")) {
                            Timber.e("Error In IranKishCardReader \n" + msg);
                            td.setPaymentFailure("", Currency.ZERO, msg);
                            result.onFailure(td, td.ErrorString);
                        } else {
                            Timber.e("Error In IranKishCardReader, result not success");
                            td.setPaymentFailure("", Currency.ZERO, activity.getString(R.string.error_in_payment_system));
                            result.onFailure(td, td.ErrorString);
                        }
                    }
                } else {
                    Timber.e("IranKishCardReader data doesn't have extras.");
                    td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_payment_system));
                    result.onFailure(td, td.ErrorString);
                }
        }
    }

    private boolean installedOrNot(Context cnt, String packageName) {
        PackageManager pm = cnt.getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (Exception e) {
            e.printStackTrace();
            appInstalled = false;
        }
        return appInstalled;
    }
}