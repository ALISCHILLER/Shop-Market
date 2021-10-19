package com.varanegar.framework.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Created by A.Torabi on 1/8/2019.
 */

public class LocaleHelper {

    public static Locale getPreferredLocal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferred_local", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", null);
        String country = sharedPreferences.getString("country", null);
        if (language == null)
            return null;
        else if (language.equals("device")) {
            return Locale.getDefault();
        } else {
            return new Locale(language, country);
        }
    }

    public static void setPreferredLocal(Context context, Locale local) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferred_local", Context.MODE_PRIVATE);
        if (local != null) {
            sharedPreferences.edit().putString("language", local.getLanguage()).apply();
            sharedPreferences.edit().putString("country", local.getCountry()).apply();
        } else {
            sharedPreferences.edit().putString("language", "device").apply();
            sharedPreferences.edit().putString("country", "device").apply();
        }
    }

    /**
     * Set the app's locale to the one specified by the given String.
     *
     * @param context
     * @param localeSpec a locale specification as used for Android resources (NOTE: does not
     *                   support country and variant codes so far); the special string "system" sets
     *                   the locale to the locale specified in system settings
     * @return
     */
    public static Context setLocale(Context context, String localeSpec) {
        Locale locale;
        if (localeSpec.equals("system")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                //noinspection deprecation
                locale = Resources.getSystem().getConfiguration().locale;
            }
        } else {
            locale = new Locale(localeSpec);
        }
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, locale);
        } else {
            return updateResourcesLegacy(context, locale);
        }
    }

    private static Context updateResources(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            configuration.setLayoutDirection(locale);
        }

        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return context.createConfigurationContext(configuration);
        else
            return context;
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
