package com.varanegar.framework.validation;

/**
 * Created by atp on 3/7/2017.
 */

public class ValidationError {
    private Object fieldObject;

    public Class<? extends ValidationChecker> getViolation() {
        return violation.getValidationChecker().getClass();
    }

    public String getFieldTitle() {
        return violation.getFieldTitle();
    }

    private ConstraintViolation violation;

    public ValidationError(ConstraintViolation violation, Object fieldObject) {
        this.violation = violation;
        this.fieldObject = fieldObject;
    }

    public Object getField() {
        return fieldObject;
    }

}
