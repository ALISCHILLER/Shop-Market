package com.varanegar.vaslibrary.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by A.Torabi on 10/1/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface SubsystemTypes {
    SubsystemTypeId[] ids();
}
