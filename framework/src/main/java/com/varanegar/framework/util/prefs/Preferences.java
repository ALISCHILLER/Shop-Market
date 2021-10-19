package com.varanegar.framework.util.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.UUID;

import timber.log.Timber;

public class Preferences {
    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    private String createKey(@Nullable UUID customerId, @Nullable UUID callId, @Nullable UUID extraId) {
        String key = null;
        if (customerId != null)
            key += customerId.toString();
        if (callId != null)
            key += "/" + callId.toString();
        if (extraId != null)
            key += "/" + extraId.toString();

        if (key != null)
            return key;
        else
            throw new IllegalStateException("");

    }

    public void remove(@NonNull PrefSet set, @Nullable UUID customerId, @Nullable UUID callId, @Nullable UUID extraId) {
        SharedPreferences sp = context.getSharedPreferences(set.getId(), Context.MODE_PRIVATE);
        String key = createKey(customerId, callId, extraId);
        sp.edit().remove(key).apply();
    }

    public void putBoolean(@NonNull PrefSet set, @Nullable UUID customerId, @Nullable UUID callId, @Nullable UUID extraId, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(set.getId(), Context.MODE_PRIVATE);
        String key = createKey(customerId, callId, extraId);
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(@NonNull PrefSet set, @Nullable UUID customerId, @Nullable UUID callId, @Nullable UUID extraId, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(set.getId(), Context.MODE_PRIVATE);
        String key = createKey(customerId, callId, extraId);
        return sp.getBoolean(key, defaultValue);
    }

    public void putString(@NonNull PrefSet set, @Nullable UUID customerId, @Nullable UUID callId, @Nullable UUID extraId, String value) {
        SharedPreferences sp = context.getSharedPreferences(set.getId(), Context.MODE_PRIVATE);
        String key = createKey(customerId, callId, extraId);
        sp.edit().putString(key, value).apply();
    }

    public String getString(@NonNull PrefSet set, @Nullable UUID customerId, @Nullable UUID callId, @Nullable UUID extraId, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(set.getId(), Context.MODE_PRIVATE);
        String key = createKey(customerId, callId, extraId);
        return sp.getString(key, defaultValue);
    }

    public void clearPreferences(PrefSet prefSet) {
        SharedPreferences sp = context.getSharedPreferences(prefSet.getId(), Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    public void clearAfterTourPreferences() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field :
                fields) {
            if (field.getType() == PrefSet.class) {
                try {
                    PrefSet set = (PrefSet) field.get(this);
                    if (set.isDeleteAfterTour()) {
                        SharedPreferences sp = context.getSharedPreferences(set.getId(), Context.MODE_PRIVATE);
                        sp.edit().clear().apply();
                    }
                } catch (IllegalAccessException e) {
                    Timber.e(e);
                }
            }
        }
    }


    public static final PrefSet InvoiceSelection = new PrefSet("INVOICE_SELECTION", true);
    public static final PrefSet UserPreferences = new PrefSet("USER_PREFERENCES", false);

}
