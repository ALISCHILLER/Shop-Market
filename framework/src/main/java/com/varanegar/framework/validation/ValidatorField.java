package com.varanegar.framework.validation;

import java.util.Arrays;
import java.util.List;

public class ValidatorField {
    Object fieldObject;
    String title;
    List<ValidationChecker> rules;

    public ValidatorField(Object fieldObject, String title, ValidationChecker... validationClass) {
        this.fieldObject = fieldObject;
        this.title = title;
        rules = Arrays.asList(validationClass);
    }
}
