package com.varanegar.framework.base.questionnaire.controls.optionscontrol;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class OptionControl {
    public String name;
    public String value;
    public boolean selected;
    public UUID UniqueId;
    public boolean hasAttachment;

    @Override
    public String toString() {
        return name;
    }
}
