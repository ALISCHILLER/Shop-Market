package com.varanegar.vaslibrary.ui.fragment.settlement.invoiceinfo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by A.Torabi on 7/28/2018.
 */

public class CurrencyInputDialog extends CuteDialog {
    private Currency max;
    private TextView amountTextView;

    public OnInputDoneListener onInputDoneListener;
    private Currency defaultValue;

    public interface OnInputDoneListener{
        void done(Currency value);

        void canceled();
    }
    public void setArguments(Currency defaultValue , Currency maxValue) {
        this.defaultValue = defaultValue;
        this.max = maxValue;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSizingPolicy(SizingPolicy.WrapContent);
        View view = inflater.inflate(R.layout.currency_input_layout, container, false);
        view.findViewById(R.id.button1).setOnClickListener(numPadListener);
        view.findViewById(R.id.button2).setOnClickListener(numPadListener);
        view.findViewById(R.id.button3).setOnClickListener(numPadListener);
        view.findViewById(R.id.button4).setOnClickListener(numPadListener);
        view.findViewById(R.id.button5).setOnClickListener(numPadListener);
        view.findViewById(R.id.button6).setOnClickListener(numPadListener);
        view.findViewById(R.id.button7).setOnClickListener(numPadListener);
        view.findViewById(R.id.button8).setOnClickListener(numPadListener);
        view.findViewById(R.id.button9).setOnClickListener(numPadListener);
        view.findViewById(R.id.button0).setOnClickListener(numPadListener);
        view.findViewById(R.id.back_image_view).setOnClickListener(numPadListener);

        amountTextView = view.findViewById(R.id.amount_text_view);
        view.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = amountTextView.getText().toString();
                try {
                    Currency currency = Currency.parse(val);
                    if (onInputDoneListener != null)
                        onInputDoneListener.done(currency);
                    dismiss();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onInputDoneListener != null)
                    onInputDoneListener.canceled();
                dismiss();
            }
        });
        oldValue = String.valueOf(defaultValue);
        amountTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                amountTextView.removeTextChangedListener(this);
                String str = s.toString().replaceAll("[^\\d]", "");
                if (str != null && !str.isEmpty()) {
                    str = HelperMethods.convertToEnglishNumbers(str);
                    if (str != null && !str.isEmpty()) {
                        double s1 = Double.parseDouble(str);
                        NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
                        ((DecimalFormat) nf2).applyPattern("###,###.###");
                        String result = nf2.format(s1);
                        s.replace(0, s.length(), result);
                    }
                }
                amountTextView.addTextChangedListener(this);
            }
        });
        amountTextView.setText(oldValue);
        return view;
    }

    private String oldValue = "";
    View.OnClickListener numPadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String newValue = oldValue;

            if (view.getId() == R.id.button1)
                newValue = newValue + "1";
            else if (view.getId() == R.id.button2)
                newValue = newValue + "2";
            else if (view.getId() == R.id.button3)
                newValue = newValue + "3";
            else if (view.getId() == R.id.button4)
                newValue = newValue + "4";
            else if (view.getId() == R.id.button5)
                newValue = newValue + "5";
            else if (view.getId() == R.id.button6)
                newValue = newValue + "6";
            else if (view.getId() == R.id.button7)
                newValue = newValue + "7";
            else if (view.getId() == R.id.button8)
                newValue = newValue + "8";
            else if (view.getId() == R.id.button9)
                newValue = newValue + "9";
            else if (view.getId() == R.id.button0) {
                if (!newValue.isEmpty())
                    newValue = newValue + "0";
            } else if (view.getId() == R.id.back_image_view) {
                int end = newValue.length() - 1;
                if (end <= 0)
                    newValue = "";
                else if (end > 0)
                    newValue = newValue.substring(0, end);
            }
            if (!newValue.isEmpty()) {
                while (!newValue.isEmpty() && newValue.startsWith("0")) {
                    if (newValue.length() > 1)
                        newValue = newValue.replace("0", "");
                    else
                        break;
                }
            }
            if (newValue.isEmpty())
                newValue = "0";

            try {
                Currency value = Currency.parse(newValue);
                if (value.compareTo(max) <= 0) {
                    oldValue = newValue;
                    amountTextView.setText(oldValue);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    };

}
