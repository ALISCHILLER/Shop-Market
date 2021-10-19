package com.varanegar.framework.network.gson.typeadapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/19/2017.
 */

public class NestleDateDeSerializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (element == null || element.isJsonNull())
            return null;
        String date = element.getAsString();
        if (date == null || date.isEmpty())
            return null;
        if (date.startsWith("12/31/9999"))
            return null;
        date.replaceAll(".", "/");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        format.setTimeZone(TimeZone.getDefault());
        try {
            return format.parse(date);
        } catch (ParseException exp) {
            Timber.e("Failed to parse Nestle Date:", exp);
            return null;
        }
    }
}
