package com.varanegar.vaslibrary.ui.dialog;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.canvertType.ConvertFaNumType;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ControlPaymentException;

/**
 * Created by e.hashemzadeh on 20/06/30.
 */

public class InsertPinDialog extends CuteDialogWithToolbar {
    String pinCode;

    public void setValues(String pinCode) {
        this.pinCode = pinCode;
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.insert_pin_dialog, container, false);
        setTitle(R.string.please_insert_pin_code);
        final PairedItemsEditable pinCodePairItem = view.findViewById(R.id.pin_paired_items);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        TextView cancelTv = view.findViewById(R.id.cancel_text_view);

        cancelTv.setOnClickListener(view12 -> {
            onResult.failed(getString(R.string.operation_canceled));
            dismiss();
        });
        okTv.setOnClickListener(view1 -> {
           String convertPinCode= ConvertFaNumType.convert(pinCodePairItem.getValue());

            if (convertPinCode.equals(pinCode))
                onResult.done();
            else
                onResult.failed(getString(R.string.pin_code_in_not_correct));
            dismiss();
        });
        return view;
    }

    public void setOnResult(OnResult onResult){
        this.onResult = onResult;
    }

    private OnResult onResult;

    public interface OnResult {
        void done() ;
        void failed(String error);
    }
}
