package com.varanegar.framework.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.WindowManager;
import android.webkit.URLUtil;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.java.util.Currency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by atp on 6/13/2017.
 */

public class HelperMethods {

    public static boolean isLowMemory() {
        return isLowMemory(0.1f);
    }

    /**
     * @param threshold a values between 0 and 1
     * @return true if percentage of free memory is lower than the threshold
     */
    public static boolean isLowMemory(float threshold) {
        Runtime runtime = Runtime.getRuntime();
        double max = (double) runtime.totalMemory();
        double free = (double) runtime.freeMemory();
        boolean lowMemory = (free / max) < threshold;
        if (lowMemory) {
            Timber.d("Application is in low memory," + " free memory = " + free + " bytes; " + " total memory = " + max + " bytes;");
        }
        return lowMemory;
    }

    @Nullable
    public static String removeLastChar(@Nullable String str) {
        if (str == null) return null;
        if (str.length() > 0 && str.charAt(str.length() - 1) == 'x') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    @Nullable
    public static String persian2Arabic(@Nullable String str) {
        if (str == null) return null;
        str = str.replace("ی", "ي");
        str = str.replace("ک", "ك");
        return str;
    }

    @Nullable
    public static String convertToEnglishNumbers(@Nullable String str) {
        if (str == null) return null;
        return Currency.convertToEnglishDigits(str);
    }

    public static String convertToOrFromPersianDigits(String str) {
        if (str == null) {
            return null;
        } else {
            StringBuilder output = new StringBuilder();

            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                switch (ch) {
                    case '0':
                        output.append('٠');
                        break;
                    case '1':
                        output.append('١');
                        break;
                    case '2':
                        output.append('٢');
                        break;
                    case '3':
                        output.append('٣');
                        break;
                    case '4':
                        output.append('٤');
                        break;
                    case '5':
                        output.append('٥');
                        break;
                    case '6':
                        output.append('٦');
                        break;
                    case '7':
                        output.append('٧');
                        break;
                    case '8':
                        output.append('٨');
                        break;
                    case '9':
                        output.append('٩');
                        break;
                    case ',':
                        output.append('٫');
                        break;
                    case '۰':
                        output.append('0');
                        break;
                    case '۱':
                        output.append('1');
                        break;
                    case '۲':
                        output.append('2');
                        break;
                    case '۳':
                        output.append('3');
                        break;
                    case '۴':
                        output.append('4');
                        break;
                    case '۵':
                        output.append('5');
                        break;
                    case '۶':
                        output.append('6');
                        break;
                    case '۷':
                        output.append('7');
                        break;
                    case '۸':
                        output.append('8');
                        break;
                    case '۹':
                        output.append('9');
                        break;
                    default:
                        output.append(ch);
                }
            }

            return output.toString();
        }
    }

    public static int getColor(Context context, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            return context.getResources().getColor(color);
        }
    }

    public static int getColorAttr(Context context, @AttrRes int color) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(color, typedValue, true);
        @ColorInt int c = typedValue.data;
        return c;
    }

    public static String doubleToString(double value) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(value);
    }

    public static String floatToString(float value) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(value);
    }

    @NonNull
    public static String bigDecimalToString(@Nullable BigDecimal value) {
        DecimalFormat df = new DecimalFormat("#.###");
        return value == null ? "0" : df.format(value);
    }

    public static double bigDecimalToDouble(@Nullable BigDecimal value) {
        return value == null ? 0 : value.doubleValue();
    }

    public static double currencyToDouble(@Nullable Currency value) {
        return value == null ? 0 : value.doubleValue();
    }

    public static BigDecimal currencyToBigDecimal(@Nullable Currency value) {
        return value == null ? BigDecimal.ZERO : value.bigDecimalValue();
    }

    public static int currencyToInt(@Nullable Currency value) {
        return value == null ? 0 : value.intValue();
    }

    @NonNull
    public static String currencyToString(@Nullable Currency value) {
        return value == null ? "0" : value.toString();
    }

    @NonNull
    public static String currencyToStringWithoutCommas(@Nullable Currency value) {
        return value == null ? "0" : value.bigDecimalValue().toString();
    }

    public static String doubleToStringWithCommas(double value) {
        DecimalFormat df = new DecimalFormat("#,###.###");
        return df.format(value);
    }

    public static Point getDisplayMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }

    public static String copyFile(String dbPath, File destinationFolder, String destinationFilename) throws IOException {
        InputStream localStream = new FileInputStream(dbPath);
        OutputStream externalStream = new FileOutputStream(destinationFolder + "/" + destinationFilename);
        //Copy the database
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = localStream.read(buffer)) > 0) {
            externalStream.write(buffer, 0, bytesRead);
        }
        //FLUSH THE OUT STREAM
        externalStream.flush();
        //Close the streams
        externalStream.close();
        localStream.close();
        return destinationFolder + "/" + destinationFilename;
    }

    public static long remainDaysBetweenTowDate(Date startDate, Date endDate) {
        long remain = TimeUnit.DAYS.convert((endDate.getTime() - startDate.getTime()), TimeUnit.MILLISECONDS);
        return remain;
    }

    public static int getDarker(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.95f;
        color = Color.HSVToColor(hsv);
        return color;
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToSp(float dp, Context context) {
        return (int) (dpToPx(dp, context) / context.getResources().getDisplayMetrics().scaledDensity);
    }


    public static boolean validateUrl(String url) {
        if (!URLUtil.isValidUrl(url))
            return false;
        try {
            URL u = new URL(url);
            u.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String convertToEnglishDigitsWitoutOtherChars(String str) {
        if (str == null) {
            return null;
        } else {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                switch (ch) {
                    case '٠':
                        output.append('0');
                        break;
                    case '١':
                        output.append('1');
                        break;
                    case '٢':
                        output.append('2');
                        break;
                    case '٣':
                        output.append('3');
                        break;
                    case '٤':
                        output.append('4');
                        break;
                    case '٥':
                        output.append('5');
                        break;
                    case '٦':
                        output.append('6');
                        break;
                    case '٧':
                        output.append('7');
                        break;
                    case '٨':
                        output.append('8');
                        break;
                    case '٩':
                        output.append('9');
                        break;
                    case '٫':
                        output.append(',');
                        break;
                    case '۰':
                        output.append('0');
                        break;
                    case '۱':
                        output.append('1');
                        break;
                    case '۲':
                        output.append('2');
                        break;
                    case '۳':
                        output.append('3');
                        break;
                    case '۴':
                        output.append('4');
                        break;
                    case '۵':
                        output.append('5');
                        break;
                    case '۶':
                        output.append('6');
                        break;
                    case '۷':
                        output.append('7');
                        break;
                    case '۸':
                        output.append('8');
                        break;
                    case '۹':
                        output.append('9');
                        break;
                }
            }
            return output.toString();
        }

    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static File getExternalFilesDir(Context context, @Nullable String folderName) {
        if (context.getExternalFilesDir(folderName).getAbsolutePath().contains(context.getPackageName()))
            return context.getExternalFilesDir(folderName);
        File file;
        if (folderName == null)
            file = context.getExternalFilesDir(context.getPackageName());
        else
            file = context.getExternalFilesDir(context.getPackageName() + "/" + folderName);
        if (!file.exists())
            file.mkdir();
        return file;
    }

    public static Currency zeroIfNull(Currency amount) {
        if (amount == null)
            return Currency.ZERO;
        return amount;
    }
}
