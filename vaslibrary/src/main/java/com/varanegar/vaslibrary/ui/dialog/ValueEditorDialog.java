package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;

import java.text.ParseException;

public class ValueEditorDialog extends CuteDialogWithToolbar {
    String strValue = "0";
    private TextView priceTextView;
    private Currency maxValue;
    private TextView label;
    private boolean isPercent;

    public void setInitialValue(Currency value) {
        if (value != null)
            strValue = value.toString();
        else
            strValue = "0";
        strValue = strValue.replaceAll(",", "");
        processValue();
    }

    public void setMaxValue(Currency value) {
        this.maxValue = value;
    }

    public void setPercentType(boolean isPercent) {
        this.isPercent = isPercent;
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSizingPolicy(SizingPolicy.Medium);
        View view = inflater.inflate(R.layout.price_editor_dialog_layout, null, false);
        priceTextView = view.findViewById(R.id.unit_price_text_view);
        priceTextView.setText(strValue);
//        setTitle(getString(R.string.current_price) + " : " + strValue);
        strValue = "";
        label = view.findViewById(R.id.label);
        if (isPercent)
            label.setText(R.string.percent);
        else
            label.setText(R.string.amount);
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
//                if (strValue == null || strValue.isEmpty() || strValue.equalsIgnoreCase("0")) {
//                    getVaranegarActvity().showSnackBar(R.string.please_input_price, MainVaranegarActivity.Duration.Short);
//                    return;
//                }
                if (onPriceChanged != null) {
                    try {
                        if (strValue == null || strValue.equals(""))
                            strValue = "0";
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
                if (view.getId() == R.id.button1 && checkValue(strValue, "1"))
                    strValue = strValue + "1";
                else if (view.getId() == R.id.button2 && checkValue(strValue, "2"))
                    strValue = strValue + "2";
                else if (view.getId() == R.id.button3 && checkValue(strValue, "3"))
                    strValue = strValue + "3";
                else if (view.getId() == R.id.button4 && checkValue(strValue, "4"))
                    strValue = strValue + "4";
                else if (view.getId() == R.id.button5 && checkValue(strValue, "5"))
                    strValue = strValue + "5";
                else if (view.getId() == R.id.button6 && checkValue(strValue, "6"))
                    strValue = strValue + "6";
                else if (view.getId() == R.id.button7 && checkValue(strValue, "7"))
                    strValue = strValue + "7";
                else if (view.getId() == R.id.button8 && checkValue(strValue, "8"))
                    strValue = strValue + "8";
                else if (view.getId() == R.id.button9 && checkValue(strValue, "9"))
                    strValue = strValue + "9";
                else if (view.getId() == R.id.button0) {
                    if (!strValue.isEmpty() && Long.parseLong(strValue) > 0 && checkValue(strValue, "0"))
                        strValue = strValue + "0";
                } else if (view.getId() == R.id.button00) {
                    if (!strValue.isEmpty() && Long.parseLong(strValue) > 0 && checkValue(strValue, "00"))
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

    private boolean checkValue(String strValue, String i) {
        return Long.valueOf(strValue.replaceAll(",", "") + i) <= maxValue.longValue();
//        return (strValue.replaceAll(",", "") + i).compareTo((Currency.convertToEnglishDigits(HelperMethods.currencyToString(maxValue))).replaceAll(",", "")) != 1;
    }

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
            for (int i = strValue.length() - 1; i >= 0; i--) {
                sb.append(strValue.charAt(i));
            }

            strValue = sb.toString();
        }
    }
}
