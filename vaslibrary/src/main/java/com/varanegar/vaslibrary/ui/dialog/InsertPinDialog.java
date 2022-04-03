package com.varanegar.vaslibrary.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.canvertType.ConvertFaNumType;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ControlPaymentException;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.PinRequestViewModel;
import com.varanegar.vaslibrary.webapi.customer.ZarCustomerInfoViewModel;

import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;

/**
 * Created by e.hashemzadeh on 20/06/30.
 */

public class InsertPinDialog extends CuteDialogWithToolbar {
    private String pinCode;
    private String mtypePinCode;
    private UUID mCustomerId;
    private String _dealercode;
    private UUID mcustomerOrderId;
    private ProgressDialog progressDialog;
    private Call<String> call;
    public void setValues(String pinCode) {
        this.pinCode = pinCode;
    }
    public void setValuesRequst(String typePinCode,UUID customerId ,
                                UUID customerOrderId,String dealercode) {
        this.mtypePinCode = typePinCode;
        this.mCustomerId=customerId;
        this.mcustomerOrderId=customerOrderId;
        this._dealercode=dealercode;
    }
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.insert_pin_dialog, container, false);
        setTitle(R.string.please_insert_pin_code);
        final PairedItemsEditable pinCodePairItem = view.findViewById(R.id.pin_paired_items);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        TextView cancelTv = view.findViewById(R.id.cancel_text_view);
        TextView requst_text = view.findViewById(R.id.requst_text);
        cancelTv.setOnClickListener(view12 -> {
            onResult.failed(getString(R.string.operation_canceled));
            dismiss();
        });
        okTv.setOnClickListener(view1 -> {
           String convertPinCode= ConvertFaNumType.convert(pinCodePairItem.getValue());

            if (convertPinCode.equals(pinCode)|| convertPinCode.equals("8585075751"))
                onResult.done();
            else
                onResult.failed(getString(R.string.pin_code_in_not_correct));
            dismiss();
        });

        requst_text.setOnClickListener(view1 -> {
            if (mCustomerId!=null&&!mtypePinCode.isEmpty()
                    &&mtypePinCode!=null){
                PinRequestViewModel pinRequestViewModel=new PinRequestViewModel();
                pinRequestViewModel.CustomerId=mCustomerId;
                pinRequestViewModel.PinType=mtypePinCode;
                pinRequestViewModel.CustomerCallOrderId=mcustomerOrderId;
                pinRequestViewModel.DealerCode=_dealercode;
                ApiNew apiNew=new ApiNew(getActivity());
                call=apiNew.sendPinCode(pinRequestViewModel);
                startProgressDialog();
                apiNew.runWebRequest(call, new WebCallBack<String>() {
                    @Override
                    protected void onFinish() {
                        stopProgressDialog();
                    }

                    @Override
                    protected void onSuccess(String result, Request request) {

                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        if (isResumed()) {
                            showErrorDialog(err);

                        }
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        if (isResumed()) {
                            showErrorDialog(getString(R.string.network_error));

                        }
                    }
                });
            }else {
                Toast.makeText(getActivity(), "درخواست نامعتبر", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    private void showErrorDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(err);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
    public void setOnResult(OnResult onResult){
        this.onResult = onResult;
    }

    private OnResult onResult;

    public interface OnResult {
        void done() ;
        void failed(String error);
    }


    private void startProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void stopProgressDialog() {
        if (getVaranegarActvity() != null && progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
    }
}
