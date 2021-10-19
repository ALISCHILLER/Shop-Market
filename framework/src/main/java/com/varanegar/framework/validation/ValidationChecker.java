package com.varanegar.framework.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public abstract class ValidationChecker<T extends Annotation> {
    public  void createRuleFromAnnotation(T annotation){

    }
    public abstract boolean validate(Object fieldObject);
}
