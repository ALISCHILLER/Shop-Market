package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

/**
 * Created by Elnaz on 6/16/2020.
 */

public class SamanKishCardReader extends DeviceCardReader implements IOnActivityResultListener {
    private String resnumber;
    VasActivity activity;
    TransactionData td;
    ICardReaderResult result;
    private Handler receiverHandler;

    public SamanKishCardReader(VasActivity activity) {
        super(activity);
        this.activity = activity;
        receiverHandler = new Handler();
    }

    @Override
    public void runTransaction(@NonNull final TransactionData td, @Nullable final ICardReaderResult result) {
        this.td = td;
        this.result = result;
        final HashMap<String, String> apportionmentList = new HashMap<String, String>();
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        resnumber = randomUUIDString;
        try {
            if (!GlobalVariables.getPahpatStatus()) {
                final ProgressDialog progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage(activity.getString(R.string.ready_payment_system));
                progressDialog.setCancelable(false);
                progressDialog.show();
                receiverHandler = new Handler();
                EasyHelper.getStatus(EasyHelper.GET_STATUS, receiverHandler, activity);
                new CountDownTimer(15000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        Log.d("onTick Pahpat", millisUntilFinished + "");
                    }
                    public void onFinish() {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (!GlobalVariables.getPahpatStatus()) {
                            Timber.e("Error In initializing SamanKishCardReader");
                            td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_initializing_payment_system));
                            result.onFailure(td, td.ErrorString);
                        } else {
                            try {
                                activity.samanKishCardReader = SamanKishCardReader.this;
                                EasyHelper.purchaseTxn(activity, receiverHandler, HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount), "", resnumber, "IRR", "FA", apportionmentList);
                            } catch (EasyHelper.PahpatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            } else {
                activity.samanKishCardReader = this;
                EasyHelper.purchaseTxn(activity, receiverHandler, HelperMethods.convertToEnglishDigitsWitoutOtherChars(td.Amount), "", resnumber, "IRR", "FA", apportionmentList);
            }
        } catch (EasyHelper.PahpatException e) {
            e.printStackTrace();
            Timber.e(e);
        }
    }

    @Override
    public void dispose() {
        if (activity != null)
            activity.samanKishCardReader = null;
    }

    @Override
    public void onReceiveResult(Context context, int requestCode, int resultCode, @Nullable Intent data, @Nullable Bundle bundle) {
        try {
            if (requestCode == EasyHelper.GOOD_CONFIRM) {
                try {
                    Timber.i("SamanKishCardReader Transaction is Successful");
                    result.onSuccess(td);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == EasyHelper.GOOD_PAYMENT) {
                if (resultCode == EasyHelper.TXN_SUCCESS) {
                    Toast.makeText(getContext(), "Success: " + bundle.getString("Reason"), Toast.LENGTH_SHORT).show();
                    String ResNum = bundle.getString("ResNum");
                    String ApprovalCode = bundle.getString("ApprovalCode");
                    try {
                        if (ResNum.equals(resnumber)) {
                            if (bundle.getString("Result").equals("erSucceed")) {
                                try {
                                    td.PaidAmount = Currency.parse(bundle.getString("Amount"));
                                    td.TransactionNo = bundle.getString("TraceNo");
                                    td.PaymentTime = new Date();
                                    EasyHelper.confirmTxn((VasActivity) context, receiverHandler, ApprovalCode, true);
                                } catch (ParseException e) {
                                    td.setPaymentFailure(bundle.getString("TraceNo"), null, getContext().getString(R.string.transaction_amount_is_not_valid));
                                    result.onFailure(td, td.ErrorString);
                                }
                            } else {
                                Timber.e("Error In SamanKishCardReader \n" + bundle.getString("Reason") + "\n");
                                td.setPaymentFailure("", Currency.ZERO, bundle.getString("Reason"));
                                result.onFailure(td, td.ErrorString);
                            }
                        } else {
                            Timber.e("Error In SamanKishCardReader \n" + bundle.getString("Reason") + "\n");
                            td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_payment_system));
                            result.onFailure(td, td.ErrorString);
                        }
                    } catch (EasyHelper.PahpatException e) {
                        Timber.e("Error In SamanKishCardReader \n" + bundle.getString("Reason") + "\n" + e);
                        td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_payment_system));
                        result.onFailure(td, td.ErrorString);
                    }

                } else {
                    if (bundle != null) {
                        Timber.e("Error In SamanKishCardReader \n" + bundle.getString("Reason"));
                        td.setPaymentFailure("", Currency.ZERO, bundle.getString("Reason"));
                        result.onFailure(td, td.ErrorString);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e("Error In SamanKishCardReader \n" + e);
            td.setPaymentFailure("", Currency.ZERO, activity.getString(com.varanegar.vaslibrary.R.string.error_in_payment_system));
            result.onFailure(td, td.ErrorString);
        }
    }
}

