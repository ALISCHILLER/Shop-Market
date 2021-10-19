package varanegar.com.discountcalculatorlib.utility;

import java.util.Calendar;
import java.util.List;

public class GlobalFunctions {
    public static<T> String CreateStringFromList(List<T> list, char delimeter)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (T t : list)
        {
            stringBuilder.append(t.toString() + delimeter);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    public static String getCurrentShamsiDate() {

        String currentDate = "";

        Calendar c = Calendar.getInstance();

        // set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        JalaliCalendar.YearMonthDate shamsiYearMonthDate = JalaliCalendar.gregorianToJalali(yearMonthDate);
        String shamsiCurrentDate = shamsiYearMonthDate.toString();
        String[] dateElements = shamsiCurrentDate.split("/");

        for (int i = 0; i < dateElements.length; i++) {
            currentDate += ((i != 0 ? "/" : "") + (dateElements[i].length() < 2 ? ("0" + dateElements[i]) : dateElements[i]));
        }

        return currentDate;
    }
}
