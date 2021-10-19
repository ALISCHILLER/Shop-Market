package com.varanegar.java.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * Created by A.Torabi on 8/1/2018.
 */

public class Currency extends Number implements Comparable<Currency> {
    private BigDecimal value;
    public static Currency ONE = new Currency(1);
    public static Currency ZERO = new Currency(0);

    public Currency(int val) {
        value = new BigDecimal(val);
    }

    public Currency(double val) {
        value = new BigDecimal(val);
    }

    public Currency(long val) {
        value = new BigDecimal(val);
    }

    public Currency(BigDecimal val) {
        value = val;
    }

    public Currency(Currency val) {
        value = new BigDecimal(val.longValue());
    }

    public static String convertToEnglishDigits(String str) {
        if (str == null) return null;
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '\u0660':
                    output.append('0');
                    break;
                case '\u06F0':
                    output.append('0');
                    break;
                case '\u06F1':
                    output.append('1');
                    break;
                case '\u0661':
                    output.append('1');
                    break;
                case '\u06F2':
                    output.append('2');
                    break;
                case '\u0662':
                    output.append('2');
                    break;
                case '\u06F3':
                    output.append('3');
                    break;
                case '\u0663':
                    output.append('3');
                    break;
                case '\u06F4':
                    output.append('4');
                    break;
                case '\u0664':
                    output.append('4');
                    break;
                case '\u06F5':
                    output.append('5');
                    break;
                case '\u0665':
                    output.append('5');
                    break;
                case '\u06F6':
                    output.append('6');
                    break;
                case '\u0666':
                    output.append('6');
                    break;
                case '\u06F7':
                    output.append('7');
                    break;
                case '\u0667':
                    output.append('7');
                    break;
                case '\u06F8':
                    output.append('8');
                    break;
                case '\u0668':
                    output.append('8');
                    break;
                case '\u06F9':
                    output.append('9');
                    break;
                case '\u0669':
                    output.append('9');
                    break;
                case '\u066B':
                    output.append(',');
                    break;
                default:
                    output.append(ch);
            }
        }
        return output.toString();
    }

    public static Currency parse(String val) throws ParseException {
        try {
            val = convertToEnglishDigits(val);
            val = val.replaceAll(",", "");
            val = val.replaceAll("٫", "");
            double d = Double.parseDouble(val);
            return new Currency(d);
        } catch (Exception ex) {
            throw new ParseException(val, 0);
        }
    }

    @Override
    public String toString() {
        if (value == null) return "";
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        return formatter.format(doubleValue());
    }

    @Override
    public int intValue() {
        return value == null ? 0 : value.intValue();
    }

    @Override
    public long longValue() {
        return value == null ? 0 : value.longValue();
    }

    @Override
    public float floatValue() {
        return value == null ? 0 : value.floatValue();
    }

    public double doubleValue() {
        return value == null ? 0 : value.doubleValue();
    }

    public BigDecimal bigDecimalValue() {
        return value == null ? BigDecimal.ZERO : value;
    }

    public Currency multiply(Currency currency) {
        return new Currency(value.multiply(currency.value));
    }

    public static Currency valueOf(double value) {
        return new Currency(value);
    }

    public Currency subtract(Currency currency) {
        return new Currency(value.subtract(currency.value));
    }

    @Override
    public int compareTo(Currency currency) {
        if (currency == null)
            throw new NullPointerException();
        return value.compareTo(currency.value);
    }

    public Currency add(Currency currency) {
        return new Currency(value.add(currency.value));
    }

    public Currency multiply(BigDecimal bigDecimal) {
        return new Currency(value.multiply(bigDecimal));
    }

    public Currency divide(Currency currency) {
        return new Currency(value.divide(currency.value));
    }

    public Currency divide(Currency currency, RoundingMode rmode) {
        return new Currency(value.divide(currency.value, rmode));
    }

    public Currency setScale(int i, int roundHalfUp) {
        return new Currency(value.setScale(i, roundHalfUp));
    }
}
