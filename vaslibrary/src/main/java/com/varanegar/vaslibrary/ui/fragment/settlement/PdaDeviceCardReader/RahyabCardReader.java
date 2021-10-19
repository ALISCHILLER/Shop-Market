package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kishcore.sdk.hybrid.api.GeneralPaymentCallback;
import com.kishcore.sdk.hybrid.api.HostApp;
import com.kishcore.sdk.hybrid.api.SDKManager;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.settlement.DeviceCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.ICardReaderResult;
import com.varanegar.vaslibrary.ui.fragment.settlement.TransactionData;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by A.Torabi on 11/11/2018.
 */

public class RahyabCardReader extends DeviceCardReader {

    private HostApp hostApp;
    private Activity activity;

    public RahyabCardReader(Activity context) {
        super(context);
        this.activity = context;
        hostApp = SDKManager.init(getContext());
    }

    @Override
    public void runTransaction(@NonNull final TransactionData td, @Nullable final ICardReaderResult result) {
        SDKManager.purchase(activity, hostApp,  HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount), "12345", 0, "-1", "", "", "", new GeneralPaymentCallback() {
            @Override
            public void onPaymentInitializationFailed(int status, String statusDescription, String reserveNumber, String maskedPan, String panHash) {
                Timber.e("Rahyab Payment initialization failed. Reserve number = " + reserveNumber);
                if (result != null) {
                    td.setPaymentFailure(null, Currency.ZERO, getContext().getString(R.string.payment_init_failed));
                    result.onFailure(td, td.ErrorString);
                }
            }

            @Override
            public void onPaymentSucceed(String terminalNo, String merchantId, String posSerial, String reserveNumber, String traceNumber, String rrn, String ref, String amount, String txnDate, String txnTime, String maskedPan, String panHash) {
                Timber.d("Rahyab payment succeeded.");
                if (result != null) {
                    try {
                        td.PaidAmount = Currency.parse(amount);
                        td.TransactionNo = traceNumber;
                        td.PaymentTime = new Date();
                        result.onSuccess(td);
                    } catch (ParseException e) {
                        td.setPaymentFailure(traceNumber, null, getContext().getString(R.string.transaction_amount_is_not_valid));
                        result.onFailure(td, td.ErrorString);
                    }
                }
            }

            @Override
            public void onPaymentFailed(int errorCode, String errorDescription, String terminalNo, String merchantId, String posSerial, String reserveNumber, String traceNumber, String rrn, String ref, String amount, String txnDate, String txnTime, String maskedPan, String panHash) {
                Timber.e("Rahyab Payment failed. Reserve number = " + reserveNumber);
                if (result != null) {
                    td.setPaymentFailure(String.valueOf(reserveNumber), Currency.ZERO, getContext().getString(R.string.payment_error) + "\n" + errorDescription);
                    result.onFailure(td, td.ErrorString);
                }
            }

            @Override
            public void onPaymentCancelled(String reserveNumber, String maskedPan, String panHash) {
                Timber.e("Rahyab Payment canceled. Reserve number = " + reserveNumber);
                if (result != null) {
                    td.setPaymentCancel(String.valueOf(reserveNumber), getContext().getString(R.string.payment_canceled));
                    result.onFailure(td, td.ErrorString);
                }
            }
        });
        /*
        SDKManager.purchase(activity, "12345", HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount), new com.kishcore.sdk.fanava.rahyab.api.PaymentCallback() {
            @Override
            public void onPaymentInitializationFailed(String reserveNumber, String maskedPan, String errorDescription, String panHash) {
                Timber.e("Rahyab Payment initialization failed. Reserve number = " + reserveNumber);
                if (result != null) {
                    td.setPaymentFailure(null, Currency.ZERO, getContext().getString(R.string.payment_init_failed));
                    result.onFailure(td, td.ErrorString);
                }
            }

            @Override
            public void onPaymentSucceed(String terminalNo, String merchantId, String posSerial, String reserveNumber, String traceNumber, String rrn, String ref, String amount, String txnDate, String txnTime, String maskedPan, String panHash) {
                Timber.d("Rahyab payment succeeded.");
                if (result != null) {
                    try {
                        td.PaidAmount = Currency.parse(amount);
                        td.TransactionNo = traceNumber;
                        td.PaymentTime = new Date();
                        result.onSuccess(td);
                    } catch (ParseException e) {
                        td.setPaymentFailure(traceNumber, null, getContext().getString(R.string.transaction_amount_is_not_valid));
                        result.onFailure(td, td.ErrorString);
                    }
                }
            }

            @Override
            public void onPaymentFailed(int errorCode, String errorDescription, String terminalNo, String merchantId, String posSerial, String reserveNumber, String traceNumber, String rrn, String ref, String amount, String txnDate, String txnTime, String maskedPan, String panHash) {
                Timber.e("Rahyab Payment failed. Reserve number = " + reserveNumber);
                if (result != null) {
                    td.setPaymentFailure(String.valueOf(reserveNumber), Currency.ZERO, getContext().getString(R.string.rahyab_payment_general_error));
                    result.onFailure(td, td.ErrorString);
                }
            }

            @Override
            public void onPaymentCancelled(String reserveNumber, String maskedPan, String panHash) {
                Timber.e("Rahyab Payment canceled. Reserve number = " + reserveNumber);
                if (result != null) {
                    td.setPaymentCancel(String.valueOf(reserveNumber), getContext().getString(R.string.payment_canceled));
                    result.onFailure(td, td.ErrorString);
                }
            }
        });
         */
    }

    @Override
    public void dispose() {

    }

}
