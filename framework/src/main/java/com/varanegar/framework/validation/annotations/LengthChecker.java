package com.varanegar.framework.validation.annotations;

import android.widget.EditText;

import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.validation.ValidationChecker;
import com.varanegar.framework.validation.WrongValidatorException;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public class LengthChecker extends ValidationChecker<Length> {
    private int min;
    private int max;
    private boolean isMandatory;

    public LengthChecker(int min, int max, boolean isMandatory){
        this.min = min;
        this.max = max;
        this.isMandatory = isMandatory;
    }

    @Override
    public void createRuleFromAnnotation(Length annotation) {
        min = annotation.min();
        max = annotation.max();
        isMandatory = annotation.isMandatory();
    }

    @Override
    public boolean validate(Object fieldObject) {
        if (fieldObject == null)
            return false;

        if (fieldObject instanceof EditText) {
            EditText editText = (EditText) fieldObject;
            int length = editText.getText().toString().length();
            if (!isMandatory && length == 0)
                return true;
            return !editText.isEnabled() || length >= min && length <= max;
        } else if (fieldObject instanceof String) {
            String string = (String) fieldObject;
            int length = string.length();
            if (!isMandatory && length == 0)
                return true;
            return length >= min && length <= max;
        } else if (fieldObject instanceof PairedItemsEditable) {
            PairedItemsEditable item = (PairedItemsEditable) fieldObject;
            String value = item.getValue();
            int length = 0;
            if (value != null)
                length = value.length();
            if (!isMandatory && length == 0)
                return true;
            return !item.isEnabled() || value != null && value.length() >= min && value.length() <= max;
        } else
            throw new WrongValidatorException(fieldObject.getClass());
    }
}
