package com.varanegar.framework.validation.annotations;

import com.varanegar.framework.validation.Validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by A.Torabi on 8/30/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Validation(checkerClass = IraniNationalCodeChecker.class)
public @interface IraniNationalCode {
}
