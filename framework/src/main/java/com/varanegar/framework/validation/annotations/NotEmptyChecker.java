package com.varanegar.framework.validation.annotations;

import android.widget.EditText;

import com.varanegar.framework.base.MutableData;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.validation.ValidationChecker;
import com.varanegar.framework.validation.WrongValidatorException;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public class NotEmptyChecker extends ValidationChecker<NotEmpty> {


    @Override
    public boolean validate(Object fieldObject) {
        if (fieldObject == null)
            return false;
        if (fieldObject instanceof EditText) {
            EditText editText = (EditText) fieldObject;
            return !editText.isEnabled() || !editText.getText().toString().isEmpty();
        } else if (fieldObject instanceof String) {
            String string = (String) fieldObject;
            return !string.isEmpty();
        } else if (fieldObject instanceof PairedItemsEditable) {
            PairedItemsEditable item = (PairedItemsEditable) fieldObject;
            return !item.isEnabled() || item.getValue() != null && !item.getValue().isEmpty();
        } else if (fieldObject instanceof PairedItemsSpinner) {
            PairedItemsSpinner item = (PairedItemsSpinner) fieldObject;
            return !item.isEnabled() || item.getSelectedItem() != null;
        } else if (fieldObject instanceof MutableData) {
            MutableData data = (MutableData) fieldObject;
            if (data.getData() == null)
                return false;
            else {
                if (data.getData() instanceof String) {
                    String d = (String) data.getData();
                    return d != null && !d.isEmpty();
                } else
                    return data.getData() != null;
            }
        } else
            throw new WrongValidatorException(fieldObject.getClass());

    }
}
