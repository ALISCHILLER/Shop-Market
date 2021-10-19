package com.varanegar.vaslibrary.ui.report.target;

import java.util.Date;

/**
 * Created by A.Jafarzadeh on 2/21/2018.
 */

public class TargetHelperMethods {
    public static String calculateAchievedPercentInDay(double target, double acheived, Date startTime, Date endTime) {
        double result = ((acheived / (((double) target / ((endTime.getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000)))) * ((new Date().getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000))) * 100);
        if (result > 100) result = 100;
        return String.valueOf(round(result, 2));
    }

    public static String calculatedAchievedPercentInPeriod(double target, double acheived) {
        double result = ((acheived / (double) target) * 100);
        if (result > 100) result = 100;
        return String.valueOf(round(result, 2));
    }

    public static String calculateAverageSaleInRestDays(double target, double acheived, Date endTime) {
        double result = ((target - acheived) / ((double) endTime.getTime() - new Date().getTime() / (24 * 60 * 60 * 1000)));
        return String.valueOf(round(result, 2));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}