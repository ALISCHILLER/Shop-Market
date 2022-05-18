package com.varanegar.framework.validation.annotations;

import com.varanegar.framework.validation.Validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Validation(checkerClass = IraniIdCodeChecker.class)
public @interface IraniIdCode {
}
