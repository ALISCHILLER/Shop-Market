package com.varanegar.framework.base.questionnaire.validator;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.R;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionsFormControl;
import com.varanegar.framework.util.Linq;

import java.util.List;

/**
 * Created by A.Torabi on 11/2/2017.
 */

public class AttachmentValidator extends ControlValidator {
    private int numberOfOptions;

    public AttachmentValidator(int numberOfOptions) {
        this.numberOfOptions = numberOfOptions;
    }

    @Override
    public boolean validate(FormControl control) {
        if (control instanceof OptionsFormControl) {
            OptionsFormControl optionsFormControl = (OptionsFormControl) control;
            List<OptionControl> selectedOptions = Linq.findAll(optionsFormControl.options, new Linq.Criteria<OptionControl>() {
                @Override
                public boolean run(OptionControl item) {
                    return item.selected;
                }
            });
            return !(selectedOptions.size() >= this.numberOfOptions && control.AttachmentId == null);
        } else
            return control.AttachmentId != null;
    }

    @NonNull
    @Override
    public String getMessage(@NonNull Context context) {
        return context.getString(R.string.attachment_is_mandatory);
    }

}
