package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.vaslibrary.R;

/**
 * Created by A.Torabi on 9/18/2017.
 */

public class SingleCustomerOrderReview extends CuteDialogWithToolbar {
    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_single_customer_review,container,false);
        return view;
    }
}
