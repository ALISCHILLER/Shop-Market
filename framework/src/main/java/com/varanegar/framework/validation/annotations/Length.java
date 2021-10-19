package com.varanegar.framework.validation.annotations;

import com.varanegar.framework.validation.Validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by A.Torabi on 8/23/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Validation(checkerClass = LengthChecker.class)
public @interface Length {
    int min() default 0;

    int max() default 100;

    boolean isMandatory() default false;
}
