package com.varanegar.framework.base.questionnaire.validator;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.questionnaire.controls.FormControl;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public abstract class ControlValidator {
    public String name;

    public ControlValidator() {

    }

    /**
     * This method validates a Form control
     *
     * @param control the Control to be validated
     * @return returns true if validation is successful
     */
    public abstract boolean validate(FormControl control);

    @NonNull
    public abstract String getMessage(@NonNull Context context);
}
