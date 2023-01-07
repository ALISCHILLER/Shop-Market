package com.varanegar.framework.util.datetime;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.varanegar.framework.util.report.filter.date.DatePickerFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by atp on 3/5/2017.
 */

public class DateHelper {
    public static final Date Min = new Date(1, 1, 1);

    public static String toString(@Nullable Calendar calendar, DateFormat dateFormat) {
        if (calendar == null)
            return "";
        if (dateFormat == DateFormat.Complete) {
            return toString(calendar.get(Calendar.YEAR)) + "/" + toString(calendar.get(Calendar.MONTH) + 1) + "/" + toString(calendar.get(Calendar.DAY_OF_MONTH)) +
                    " " + toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + toString(calendar.get(Calendar.MINUTE)) + ":" + toString(calendar.get(Calendar.SECOND));
        } else if (dateFormat == DateFormat.Date) {
            return toString(calendar.get(Calendar.YEAR)) + "/" + toString(calendar.get(Calendar.MONTH) + 1) + "/" + toString(calendar.get(Calendar.DAY_OF_MONTH));
        } else if (dateFormat == DateFormat.Time) {
            return toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + toString(calendar.get(Calendar.MINUTE)) + ":" + toString(calendar.get(Calendar.SECOND));
        } else if (dateFormat == DateFormat.Simple) {
            return toString(calendar.get(Calendar.YEAR)) + toString(calendar.get(Calendar.MONTH) + 1) + toString(calendar.get(Calendar.DAY_OF_MONTH));
        } else if (dateFormat == DateFormat.MicrosoftDateTime) {
            return toString(calendar.get(Calendar.YEAR)) + "-" + toString(calendar.get(Calendar.MONTH) + 1) + "-" + toString(calendar.get(Calendar.DAY_OF_MONTH))
                    + "T" + toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + toString(calendar.get(Calendar.MINUTE)) + ":" + toString(calendar.get(Calendar.SECOND))
                    + "." + toString(calendar.get(Calendar.MILLISECOND));
        } else if (dateFormat == DateFormat.FileName) {
            return toString(calendar.get(Calendar.YEAR)) + toString(calendar.get(Calendar.MONTH) + 1) + toString(calendar.get(Calendar.DAY_OF_MONTH))
                    + "T" + toString(calendar.get(Calendar.HOUR_OF_DAY)) + toString(calendar.get(Calendar.MINUTE)) + toString(calendar.get(Calendar.SECOND))
                    + "_" + toString(calendar.get(Calendar.MILLISECOND));
        } else
            return calendar.toString();
    }

    private static String toString(int number) {
        if (number < 10)
            return "0" + Integer.toString(number);
        else
            return Integer.toString(number);
    }

    public static String toString(@Nullable Date date, DateFormat dateFormat, Locale locale) {
        if (date == null)
            return "";
        String language = locale.getLanguage();
        if (language.equals("fa")) {
            JalaliCalendar calendar = JalaliCalendar.getCalendar(date);
            return toString(calendar, dateFormat);
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return toString(calendar, dateFormat);
        }
    }

    public static void showDatePicker(@NonNull AppCompatActivity activity, @Nullable Locale locale, @NonNull final OnDateSelected onDateSelected) {
        String language = Locale.getDefault().getLanguage();
        if (locale != null)
            language = locale.getLanguage();

        if (language.equals("fa")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog();
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                    JalaliCalendar calendar = new JalaliCalendar();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    onDateSelected.run(calendar);
                }
            });
            datePickerDialog.show(activity.getFragmentManager(), "DatePickerDialog");
        } else {
            DatePickerFragment datePickerDialog = new DatePickerFragment();
            datePickerDialog.onDateSet = new DatePickerFragment.OnDateSet() {
                @Override
                public void run(int year, int month, int day) {
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.set(year, month, day);
                    onDateSelected.run(calendar);
                }
            };
            datePickerDialog.show(activity.getSupportFragmentManager(), "timePicker");
        }
    }

    /***
     *
     * @param timeOffset in seconds
     * @return
     */
    public static String getTimeSpanString(long timeOffset) {
        int hours = (int) (timeOffset / 3600);
        int m = (int) (timeOffset % 3600);
        int minutes = m / 60;
        int seconds = m % 60;
        return toString(hours) + ":" + toString(minutes) + ":" + toString(seconds);
    }
    public static String getTimeHMString(long timeOffset) {
        int hours = (int) (timeOffset / 3600);
        int m = (int) (timeOffset % 3600);
        int minutes = m / 60;
        int seconds = m % 60;
        return toString(hours) + ":" + toString(minutes);
    }

    public interface OnDateSelected {
        void run(Calendar calendar);
    }

    public final static Locale fa = new Locale("fa", "Iran");
}
