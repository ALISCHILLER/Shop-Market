package com.varanegar.framework.base.questionnaire.validator;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.R;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionsFormControl;
import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class OptionAttachmentValidator extends ControlValidator {
    public List<UUID> MandatoryOptions;

    @Override
    public boolean validate(FormControl control) {
        if (MandatoryOptions == null)
            MandatoryOptions = new ArrayList<>();

        if (control instanceof OptionsFormControl) {
            List<OptionControl> values = ((OptionsFormControl) control).options;
            List<OptionControl> selectedOptions = Linq.findAll(values, new Linq.Criteria<OptionControl>() {
                @Override
                public boolean run(OptionControl item) {
                    return item.selected;
                }
            });

            for (final OptionControl opt :
                    selectedOptions) {
                for (UUID id :
                        MandatoryOptions) {
                    if (id.equals(opt.UniqueId))
                        return control.AttachmentId != null;
                }
            }
        }
        return true;
    }

    @NonNull
    @Override
    public String getMessage(@NonNull Context context) {
        return context.getString(R.string.attachment_is_mandatory);
    }
}
