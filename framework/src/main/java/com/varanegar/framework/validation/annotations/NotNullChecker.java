package com.varanegar.framework.validation.annotations;

import com.varanegar.framework.base.MutableData;
import com.varanegar.framework.validation.ValidationChecker;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public class NotNullChecker extends ValidationChecker<NotNull> {
    @Override
    public boolean validate(Object fieldObject) {
        if (fieldObject == null)
            return false;
        else if (fieldObject instanceof MutableData)
            return ((MutableData) fieldObject).getData() != null;
        else return true;
    }
}
