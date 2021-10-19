package com.varanegar.framework.util.component;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

/**
 * Created by A.Torabi on 11/6/2018.
 */

public class UserDialogPreferences {
    public static boolean isVisible(@NonNull Context context, @NonNull String id) {
        SharedPreferences sp = context.getSharedPreferences("UserDialogPreferences", Context.MODE_PRIVATE);
        boolean available = sp.getBoolean("Prefs_Available", false);
        boolean isVisible = sp.getBoolean(id, true);
        return !available || isVisible;
    }

    public static boolean isPreferenceAvailable(@NonNull Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserDialogPreferences", Context.MODE_PRIVATE);
        return sp.getBoolean("Prefs_Available", false);
    }

    public static void setPreference(@NonNull Context context, @NonNull String id, boolean isVisible) {
        SharedPreferences sp = context.getSharedPreferences("UserDialogPreferences", Context.MODE_PRIVATE);
        sp.edit().putBoolean(id, isVisible).apply();
    }

    public static void setPreferenceAvailability(@NonNull Context context, boolean available) {
        SharedPreferences sp = context.getSharedPreferences("UserDialogPreferences", Context.MODE_PRIVATE);
        sp.edit().putBoolean("Prefs_Available", available).apply();
    }

    public static void clearPreferences(@NonNull Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserDialogPreferences", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

}
