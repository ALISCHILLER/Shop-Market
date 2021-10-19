package com.varanegar.supervisor;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.supervisor.model.VisitorModel;

public class VisitorFilter {
    public static void save(Context context, VisitorModel visitorModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VisitorFilter", Context.MODE_PRIVATE);
        String json = VaranegarGsonBuilder.build().create().toJson(visitorModel);
        sharedPreferences.edit().putString("visitor", json).commit();
    }

    @NonNull
    public static VisitorModel read(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VisitorFilter", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("visitor", null);
        if (json != null)
            return VaranegarGsonBuilder.build().create().fromJson(json, VisitorModel.class);
        else {
            VisitorModel visitorModel = new VisitorModel();
            visitorModel.Name = context.getString(R.string.all_visitors);
            return visitorModel;
        }
    }
}
