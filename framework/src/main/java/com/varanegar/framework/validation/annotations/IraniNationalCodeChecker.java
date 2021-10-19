package com.varanegar.framework.validation.annotations;

import android.widget.EditText;

import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.validation.ValidationChecker;

/**
 * Created by A.Torabi on 8/30/2017.
 */

public class IraniNationalCodeChecker extends ValidationChecker<IraniNationalCode> {
    @Override
    public boolean validate(Object fieldObject) {
        String meli_code = null;
        if (fieldObject instanceof PairedItemsEditable) {
            meli_code = ((PairedItemsEditable) fieldObject).getValue();
            if (!((PairedItemsEditable) fieldObject).isEnabled())
                return true;
        } else if (fieldObject instanceof EditText) {
            meli_code = ((EditText) fieldObject).getText().toString();
            if (!((EditText) fieldObject).isEnabled())
                return true;
        } else if (fieldObject instanceof String) {
            meli_code = (String) fieldObject;
        }
        if (meli_code == null || meli_code.isEmpty())
            return true;

        if (meli_code.length() == 10) {
            if (meli_code.equals("1111111111") ||
                    meli_code.equals("0000000000") ||
                    meli_code.equals("2222222222") ||
                    meli_code.equals("3333333333") ||
                    meli_code.equals("4444444444") ||
                    meli_code.equals("5555555555") ||
                    meli_code.equals("6666666666") ||
                    meli_code.equals("7777777777") ||
                    meli_code.equals("8888888888") ||
                    meli_code.equals("9999999999") ||
                    meli_code.equals("0123456789")
                    ) {
                return false;
            }

            int c = Integer.parseInt(String.valueOf(meli_code.charAt(9)));

            int n = Integer.parseInt(String.valueOf(meli_code.charAt(0))) * 10 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(1))) * 9 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(2))) * 8 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(3))) * 7 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(4))) * 6 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(5))) * 5 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(6))) * 4 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(7))) * 3 +
                    Integer.parseInt(String.valueOf(meli_code.charAt(8))) * 2;
            int r = n - (n / 11) * 11;
            if ((r == 0 && r == c) || (r == 1 && c == 1) || (r > 1 && c == 11 - r)) {
                return true;
            } else {
                return false;
            }
        } else if (meli_code.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
