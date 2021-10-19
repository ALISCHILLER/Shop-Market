package com.varanegar.framework.validation;

import java.util.List;

/**
 * Created by atp on 3/7/2017.
 */

public interface ValidationListener {
    void onValidationSucceeded();
    void onValidationFailed(List<ValidationError> errors);
}
