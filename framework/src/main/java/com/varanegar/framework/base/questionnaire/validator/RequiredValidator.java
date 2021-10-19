package com.varanegar.framework.base.questionnaire.validator;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.R;
import com.varanegar.framework.base.questionnaire.controls.BooleanControl;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.base.questionnaire.controls.NumberControl;
import com.varanegar.framework.base.questionnaire.controls.TextControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionsFormControl;
import com.varanegar.framework.util.Linq;

import java.util.List;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class RequiredValidator extends ControlValidator {


    @Override
    public boolean validate(FormControl control) {
        if (control instanceof TextControl) {
            String value = ((TextControl) control).value;
            return !(value == null || value.isEmpty());
        } else if (control instanceof OptionsFormControl) {
            List<OptionControl> values = ((OptionsFormControl) control).options;
            return Linq.exists(values, new Linq.Criteria<OptionControl>() {
                @Override
                public boolean run(OptionControl item) {
                    return item.selected;
                }
            });
        } else if (control instanceof NumberControl) {
            Integer value = ((NumberControl) control).value;
            return value != null;
        } else if (control instanceof BooleanControl) {
            Boolean value = ((BooleanControl) control).value;
            return value != null;
        } else return true;
    }

    @NonNull
    @Override
    public String getMessage(@NonNull Context context) {
        return context.getString(R.string.this_field_is_mandatory);
    }
}
