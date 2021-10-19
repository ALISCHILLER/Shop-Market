package com.varanegar.framework.validation;

import java.util.List;

/**
 * Created by atp on 12/24/2016.
 */
public interface ValidationCallBack {
    void onValidationFailed(List<ConstraintViolation> errors);
    void onValidationSucceeded();
}
