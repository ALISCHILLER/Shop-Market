package com.varanegar.framework.network.gson.typeadapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.varanegar.java.util.Currency;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Created by atp on 6/13/2017.
 */

public class CurrencyTypeAdapter implements JsonDeserializer<Currency> , JsonSerializer<Currency> {
    @Override
    public Currency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            try {
                final JsonPrimitive primitive = json.getAsJsonPrimitive();
                BigDecimal value = primitive.getAsBigDecimal();
                return new Currency(value);
            } catch (Exception ex) {
                throw new JsonParseException(ex);
            }
        } else throw new JsonParseException("Invalid currency value");
    }


    @Override
    public JsonElement serialize(Currency src, Type typeOfSrc, JsonSerializationContext context) {
        double value = src == null ? 0 : src.doubleValue();
        return new JsonPrimitive(value);
    }
}
