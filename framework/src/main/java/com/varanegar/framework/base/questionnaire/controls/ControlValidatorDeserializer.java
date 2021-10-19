package com.varanegar.framework.base.questionnaire.controls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.varanegar.framework.base.questionnaire.validator.ControlValidator;
import com.varanegar.framework.base.questionnaire.validator.RangeValidator;
import com.varanegar.framework.base.questionnaire.validator.RequiredValidator;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by A.Torabi on 7/2/2017.
 */

public class ControlValidatorDeserializer implements JsonDeserializer<ControlValidator> {
    private Gson gson;

    public ControlValidatorDeserializer() {
        gson = new GsonBuilder().create();
        validators.put("Required", RequiredValidator.class);
        validators.put("Range", RangeValidator.class);
    }

    private HashMap<String, Class<? extends ControlValidator>> validators = new HashMap<>();


    public void addValidator(String name, Class<? extends ControlValidator> clazz) {
        validators.put(name, clazz);
    }

    public HashMap<String, Class<? extends ControlValidator>> getValidators() {
        return validators;
    }

    @Override
    public ControlValidator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            String type = object.get("name").getAsString();
            if (getValidators().containsKey(type)) {
                Class<? extends ControlValidator> clazz = getValidators().get(type);
                ControlValidator model = gson.fromJson(json, clazz);
                return model;
            }
        }
        return null;
    }
}
