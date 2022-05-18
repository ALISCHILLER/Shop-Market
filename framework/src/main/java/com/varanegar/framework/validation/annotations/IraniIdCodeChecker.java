package com.varanegar.framework.validation.annotations;

import android.widget.EditText;

import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.validation.ValidationChecker;

import java.lang.reflect.Array;

public class IraniIdCodeChecker extends ValidationChecker<IraniIdCode> {

//    public boolean validate(String code) {
//        try {
//            Integer.parseInt(code, 10);
//        } catch (Exception ex) {
//            return false;
//        }
//
//        if (code.length() < 11) return false;
//
//        if (Integer.parseInt(code.substring(3, 6), 10) == 0) return false;
//
//        int c = Integer.parseInt(code.substring(10, 1), 10);
//        int d = Integer.parseInt(code.substring(9, 1), 10) + 2;
//        int[] z = {29, 27, 23, 19, 17};
//        int s = 0;
//        for (int i = 0; i < 10; i++)
//            s += (d + Integer.parseInt(code.substring(i, 1), 10)) * z[i % 5];
//        s = s % 11;
//        if (s == 10) s = 0;
//        return (c == s);
//    }

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

        if (meli_code.length() != 11) return false;

        if (meli_code.length() == 11) {

//            if (Integer.parseInt(meli_code.substring(3, 6), 10) == 0)
//                return false;
//
//            int c = Integer.parseInt(meli_code.substring(10, 1), 10);
//            int d = Integer.parseInt(meli_code.substring(9, 1), 10) + 2;
//            int[] z = {29, 27, 23, 19, 17};
//            int s = 0;
//            for (int i = 0; i < 10; i++)
//                s += (d + Integer.parseInt(meli_code.substring(i, 1), 10)) * z[i % 5];
//            s = s % 11;
//            if (s == 10) s = 0;
//
//            if (c == s)
//                return true;
//            else
//                return false;
            String nationalCodeWithoutControlDigit = meli_code.substring(0, meli_code.length() - 1);
            String controlDigit = meli_code.substring(meli_code.length() - 1, meli_code.length());
            String deci = meli_code.substring(meli_code.length() - 2, meli_code.length() - 1);
            int decimal = Integer.parseInt(deci) + 2;
            int multiplier[] = {29, 27, 23, 19, 17, 29, 27, 23, 19, 17};
            int sum = 0;
            int i = 0;
            for (char c : nationalCodeWithoutControlDigit.toCharArray()) {
                int temp = (Integer.parseInt("" + c) + decimal) * multiplier[i];
                i++;
                sum += temp;
            }
            int modBy11 = sum % 11;
            if (modBy11 == 10) {
                modBy11 = 0;
            }

            if (modBy11 == Integer.parseInt(controlDigit))
                return true;

            return false;
        }else if (meli_code.length() == 0) {
            return true;
        } else {
            return false;
        }


    }
}
