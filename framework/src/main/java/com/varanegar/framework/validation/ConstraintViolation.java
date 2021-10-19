package com.varanegar.framework.validation;

import java.lang.reflect.Field;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public class ConstraintViolation {
    public String getFieldTitle() {
        return fieldTitle;
    }

    private final String fieldTitle;

    public ValidationChecker getValidationChecker() {
        return validationChecker;
    }

    private final ValidationChecker validationChecker;

    public Object getFieldObject() {
        return fieldObject;
    }

    private Object fieldObject;

    public ConstraintViolation(ValidationChecker validationChecker, Object field , String fieldTitle) {
        this.validationChecker = validationChecker;
        this.fieldObject = field;
        this.fieldTitle = fieldTitle;
    }

    public String getLog() {
        return "Constraint Violation on field";
    }

}
