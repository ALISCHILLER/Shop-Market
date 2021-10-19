package com.varanegar.framework.validation.annotations;

import android.widget.EditText;

import com.varanegar.framework.base.MutableData;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.validation.ValidationChecker;
import com.varanegar.framework.validation.WrongValidatorException;

/**
 * Created by A.Torabi on 8/24/2017.
 */

public class PhoneNumberChecker extends ValidationChecker<PhoneNumber> {
    private boolean isMandatory;

    @Override
    public void createRuleFromAnnotation(PhoneNumber annotation) {
        isMandatory = annotation.isMandatory();
    }

    @Override
    public boolean validate(Object fieldObject) {
        if (fieldObject == null)
            return false;

        if (fieldObject instanceof String)
            return validatePhoneNumber((String) fieldObject, isMandatory);
        if (fieldObject instanceof MutableData)
            return validatePhoneNumber(((MutableData<String>) fieldObject).getData(), isMandatory);
        else if (fieldObject instanceof EditText)
            return !((EditText) fieldObject).isEnabled() || validatePhoneNumber(((EditText) fieldObject).getText().toString(), isMandatory);
        else if (fieldObject instanceof PairedItemsEditable)
            return !((PairedItemsEditable) fieldObject).isEnabled() || validatePhoneNumber(((PairedItemsEditable) fieldObject).getValue(), isMandatory);
        else
            throw new WrongValidatorException(fieldObject.getClass());

    }

    private static boolean validatePhoneNumber(String phoneNo, boolean isMandatory) {
        if ((phoneNo == null || phoneNo.length() == 0) && !isMandatory)
            return true;

        if (phoneNo.length() < 3)
            return false;
        return phoneNo.matches(".*\\d+.*");

    }
}
