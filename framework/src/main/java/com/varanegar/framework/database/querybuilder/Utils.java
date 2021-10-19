package com.varanegar.framework.database.querybuilder;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.projection.Projection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    public static final List<Object> EMPTY_LIST = new ArrayList<Object>();

    public static String toString(Object value) {
        if (value == null)
            return null;

        if (value instanceof String)
            return (String) value;
        else if (value instanceof Float)
            return new BigDecimal((Float) value).stripTrailingZeros().toPlainString();
        else if (value instanceof Double)
            return new BigDecimal((Double) value).stripTrailingZeros().toPlainString();
        else
            return String.valueOf(value);
    }

    public static Long dateToLong(Date date) {
        if (date == null)
            return null;
        return date.getTime();
    }

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.length() <= 0);
    }

    public static boolean isNullOrWhiteSpace(String string) {
        return (string == null || string.trim().length() <= 0);
    }

    public static Projection[] buildColumnProjections(ModelProjection... columns) {
        Projection[] projections = new Projection[columns.length];

        for (int i = 0; i < columns.length; i++) {
            projections[i] = Projection.column(columns[i]);
        }
        return projections;
    }
}
