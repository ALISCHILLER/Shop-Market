package com.varanegar.vaslibrary.manager.canvertType;

public class ConvertFaNumType {

    public static String convert(String faNumbers) {
        String[][] mChars = new String[][]{
                {"۰", "0"},
                {"۱", "1"},
                {"۲", "2"},
                {"۳", "3"},
                {"۴", "4"},
                {"۵", "5"},
                {"۶", "6"},
                {"۷", "7"},
                {"۸", "8"},
                {"۹", "9"}
        };

        for (String[] num : mChars) {

            faNumbers = faNumbers.replace(num[0], num[1]);

        }

        return faNumbers;
    }
}
