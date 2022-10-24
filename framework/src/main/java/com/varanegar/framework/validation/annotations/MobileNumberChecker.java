package com.varanegar.framework.validation.annotations;

import android.widget.EditText;

import com.varanegar.framework.base.MutableData;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.validation.ValidationChecker;
import com.varanegar.framework.validation.WrongValidatorException;

/**
 * Created by m-latifi on 10/24/2022
 */

public class MobileNumberChecker extends ValidationChecker<MobileNumber> {


    //---------------------------------------------------------------------------------------------- validate
    @Override
    public boolean validate(Object fieldObject) {
        if (fieldObject == null)
            return false;

        if (fieldObject instanceof String)
            return validateMobileNumber((String) fieldObject);
        if (fieldObject instanceof MutableData)
            return validateMobileNumber(((MutableData<String>) fieldObject).getData());
        else if (fieldObject instanceof EditText)
            return !((EditText) fieldObject).isEnabled() || validateMobileNumber(((EditText) fieldObject).getText().toString());
        else if (fieldObject instanceof PairedItemsEditable)
            return !((PairedItemsEditable) fieldObject).isEnabled() || validateMobileNumber(((PairedItemsEditable) fieldObject).getValue());
        else
            throw new WrongValidatorException(fieldObject.getClass());

    }
    //---------------------------------------------------------------------------------------------- validate


    //---------------------------------------------------------------------------------------------- validateMobileNumber
    private static boolean validateMobileNumber(String mobileNo) {
        if ((mobileNo == null || mobileNo.length() == 0))
            return false;

        return mobileNo.matches("^(09)\\d{9}");

    }
    //---------------------------------------------------------------------------------------------- validateMobileNumber
}
