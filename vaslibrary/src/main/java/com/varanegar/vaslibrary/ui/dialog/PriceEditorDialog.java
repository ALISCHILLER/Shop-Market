package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;

import java.text.ParseException;

public class PriceEditorDialog extends CuteDialogWithToolbar {
    String strValue = "0";
    private TextView priceTextView;

    public void setInitialValue(Currency value){
        strValue = value.toString();
        strValue = strValue.replaceAll(",", "");
        processValue();
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSizingPolicy(SizingPolicy.Medium);
        View view = inflater.inflate(R.layout.price_editor_dialog_layout, null, false);
        priceTextView = view.findViewById(R.id.unit_price_text_view);
        priceTextView.setText(strValue);
        setTitle(getString(R.string.current_price) + " : "+ strValue );
        strValue = "";
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
        view.findViewById(R.id.button00).setOnClickListener(numPadListener);
        view.findViewById(R.id.clear_image_button).setOnClickListener(numPadListener);
        view.findViewById(R.id.back_image_button).setOnClickListener(numPadListener);
        view.findViewById(R.id.done_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strValue == null || strValue.isEmpty() || strValue.equalsIgnoreCase("0")) {
                    getVaranegarActvity().showSnackBar(R.string.please_input_price, MainVaranegarActivity.Duration.Short);
                    return;
                }
                if (onPriceChanged != null) {
                    try {
                        Currency value = Currency.parse(strValue);
                        onPriceChanged.run(value);
                        dismiss();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return view;
    }

    public void setPriceChangedListener(OnPriceChanged onPriceChanged) {
        this.onPriceChanged = onPriceChanged;
    }

    private OnPriceChanged onPriceChanged;

    public interface OnPriceChanged {
        void run(Currency value);
    }


    View.OnClickListener numPadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            strValue = strValue.replaceAll(",", "");
            if (strValue.length() < 15) {
                if (view.getId() == R.id.button1)
                    strValue = strValue + "1";
                else if (view.getId() == R.id.button2)
                    strValue = strValue + "2";
                else if (view.getId() == R.id.button3)
                    strValue = strValue + "3";
                else if (view.getId() == R.id.button4)
                    strValue = strValue + "4";
                else if (view.getId() == R.id.button5)
                    strValue = strValue + "5";
                else if (view.getId() == R.id.button6)
                    strValue = strValue + "6";
                else if (view.getId() == R.id.button7)
                    strValue = strValue + "7";
                else if (view.getId() == R.id.button8)
                    strValue = strValue + "8";
                else if (view.getId() == R.id.button9)
                    strValue = strValue + "9";
                else if (view.getId() == R.id.button0) {
                    if (!strValue.isEmpty() && Long.parseLong(strValue) > 0)
                        strValue = strValue + "0";
                } else if (view.getId() == R.id.button00) {
                    if (!strValue.isEmpty() && Long.parseLong(strValue) > 0)
                        strValue = strValue + "00";
                }
            }


            if (view.getId() == R.id.back_image_button) {
                int end = strValue.length() - 1;
                if (end <= 0)
                    strValue = "0";
                else if (end > 0)
                    strValue = strValue.substring(0, end);
            } else if (view.getId() == R.id.clear_image_button) {
                strValue = "0";
            }
            if (!strValue.isEmpty()) {
                strValue = Long.toString(Long.parseLong(strValue));
            }
            if (strValue.isEmpty())
                strValue = "0";

            processValue();

            priceTextView.setText(strValue);
        }
    };

    private void processValue() {
        if (strValue.length() > 3) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = strValue.length() - 1; i >= 0; i--) {
                stringBuffer.append(strValue.charAt(i));
                int j = strValue.length() - i;
                if (j % 3 == 0 && j != strValue.length())
                    stringBuffer.append(",");
            }

            strValue = stringBuffer.toString();

            StringBuffer sb = new StringBuffer();
            for(int i = strValue.length() - 1; i >= 0; i--)
            {
                sb.append(strValue.charAt(i));
            }

            strValue = sb.toString();
        }
    }
}
