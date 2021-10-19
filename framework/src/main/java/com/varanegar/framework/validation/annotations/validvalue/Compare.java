package com.varanegar.framework.validation.annotations.validvalue;

import com.varanegar.framework.validation.Validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by A.Torabi on 8/23/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validation(checkerClass = CompareValueChecker.class)
public @interface Compare {
    Operator operator() default Operator.IsNotNull;
    String value() default "";
}

