package com.varanegar.framework.validation.annotations;

import com.varanegar.framework.validation.Validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by m-latifi on 10/24/2022
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validation(checkerClass = MobileNumberChecker.class)
public @interface MobileNumber {
}
