package com.varanegar.vaslibrary.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.vaslibrary.R;

public class StarDialog extends CuteDialogWithToolbar {

    private TextView text_star;
    private int degreeStar;
    private String customerName;
    public void setValues(int degreeStar,String customerName) {
        this.degreeStar = degreeStar;
        this.customerName=customerName;
    }
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(
                R.layout.star_dialog_layout, container, false);
        setTitle("اطلاعات فاکتور");
        text_star=view.findViewById(R.id.text_star);
        TextView okTv = view.findViewById(R.id.ok_text_view);
        text_star.setText("تعداد فاکتورهای" +" "+ customerName +" "+  "در بازه سه ماه"+" "+degreeStar+" "+"بار می باشد");


        okTv.setOnClickListener(view12 -> {
            dismiss();
        });

        return view;
    }


}
