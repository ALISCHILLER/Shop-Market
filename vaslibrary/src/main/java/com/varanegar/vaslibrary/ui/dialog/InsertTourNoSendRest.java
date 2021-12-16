package com.varanegar.vaslibrary.ui.dialog;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.canvertType.ConvertFaNumType;

public class InsertTourNoSendRest  extends CuteDialogWithToolbar {
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.insert_tourno_rest, container, false);

        final PairedItemsEditable pinCodePairItem = view.findViewById(R.id.pin_paired_items);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        TextView cancelTv = view.findViewById(R.id.cancel_text_view);
        cancelTv.setOnClickListener(view12 -> {
            onResultt.failed(getString(R.string.operation_canceled));
            dismiss();
        });
        okTv.setOnClickListener(view1 -> {
            String convertPinCode= ConvertFaNumType.convert(pinCodePairItem.getValue());

            if (!convertPinCode.isEmpty()&& convertPinCode !=null )
                onResultt.done(convertPinCode);
            else
                onResultt.failed(getString(R.string.TourNo_in_not_correct));
            dismiss();
        });
        return view;
    }

    public void setOnResult(OnResult onResult){
        this.onResultt = onResult;
    }

    private OnResult onResultt;

    public interface OnResult {
        void done(String tourNo) ;
        void failed(String error);
    }
}
