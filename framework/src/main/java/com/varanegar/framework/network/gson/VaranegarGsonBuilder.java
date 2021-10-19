package com.varanegar.framework.network.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;
import com.varanegar.framework.base.questionnaire.controls.ControlValidatorDeserializer;
import com.varanegar.framework.base.questionnaire.validator.ControlValidator;
import com.varanegar.framework.network.gson.annotations.Exclude;
import com.varanegar.framework.network.gson.typeadapters.CurrencyTypeAdapter;
import com.varanegar.framework.network.gson.typeadapters.DateTypeAdapter;
import com.varanegar.java.util.Currency;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by atp on 6/13/2017.
 */

public class VaranegarGsonBuilder {
    public static GsonBuilder build() {
        return build(true);
    }
    public static GsonBuilder build(boolean camelCase) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (camelCase) {
            gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {
                @Override
                public String translateName(Field f) {
                    String fieldName = f.getName();
                    return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                }
            });
        }
        gsonBuilder.addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                try {
                    Annotation annotation = f.getAnnotation(Exclude.class);
                    return annotation != null;
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                try {
                    Annotation annotation = clazz.getAnnotation(Exclude.class);
                    return annotation != null;
                } catch (Exception ex) {
                    return false;
                }
            }
        });
        gsonBuilder.registerTypeAdapter(Currency.class, new CurrencyTypeAdapter());
        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        gsonBuilder.registerTypeAdapter(ControlValidator.class, new ControlValidatorDeserializer());
        return gsonBuilder;
    }
}
