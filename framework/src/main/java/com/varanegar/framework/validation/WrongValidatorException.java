package com.varanegar.framework.validation;


import java.lang.annotation.Annotation;

/**
 * Created by atp on 3/7/2017.
 */

public class WrongValidatorException extends RuntimeException {
    public WrongValidatorException(Class targetType) {
        super("The validator is not available for " + targetType.getSimpleName());

    }
}
