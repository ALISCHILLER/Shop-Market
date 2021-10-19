package com.varanegar.processor.annotations;

import com.varanegar.processor.Generated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by atp on 12/11/2016.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Table {
    String name() default "";
    Generated uniqueId() default Generated.None;
}
