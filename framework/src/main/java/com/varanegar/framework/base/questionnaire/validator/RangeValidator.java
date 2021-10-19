package com.varanegar.framework.base.questionnaire.validator;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.R;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.base.questionnaire.controls.NumberControl;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class RangeValidator extends ControlValidator {
    public int max;
    public int min;

    @Override
    public boolean validate(FormControl control) {
        if (control instanceof NumberControl) {
            NumberControl textControl = (NumberControl) control;
            if (textControl.value == null)
                return false;
            int value = textControl.value;
            return value <= max && value >= min;
        }
        return true;
    }

    @NonNull
    @Override
    public String getMessage(@NonNull Context context) {
        return context.getString(R.string.max_and_min_value_are_respectively) + String.valueOf(max) + " , " + String.valueOf(min);
    }
}
