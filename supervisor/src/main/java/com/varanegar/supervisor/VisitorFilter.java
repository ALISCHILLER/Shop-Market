package com.varanegar.supervisor;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.utill.multispinnerfilter.KeyPairBoolData;

import java.lang.reflect.Type;
import java.util.List;

public class VisitorFilter {
    public static void save(Context context, VisitorModel visitorModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VisitorFilter", Context.MODE_PRIVATE);
        String json = VaranegarGsonBuilder.build().create().toJson(visitorModel);
        sharedPreferences.edit().putString("visitor", json).commit();
    }
    public static void saveconditionCustomer(Context context,String conditionCustomer) {
        SharedPreferences sharedconditionCustomer = context.getSharedPreferences("conditionCustomer", Context.MODE_PRIVATE);
        sharedconditionCustomer.edit().putString("condition", conditionCustomer).commit();
    }
    public static void setSaveVisitor(Context context, List<String> list) {
        SharedPreferences editor = context.getSharedPreferences("KeyPairBoolData", Context.MODE_PRIVATE);
        String json = VaranegarGsonBuilder.build().create().toJson(list);
        editor.edit().putString("visitorBoolData", json).commit();
    }
    public static void setSave_product_group(Context context, List<String> list) {
        SharedPreferences editor = context.getSharedPreferences("product_groupData", Context.MODE_PRIVATE);
        String json = VaranegarGsonBuilder.build().create().toJson(list);
        editor.edit().putString("product_group", json).commit();
    }
    public static List<String> getproduct_group(Context context){
        List<String> arrayItems = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("product_groupData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("product_group", null);
        if (json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            arrayItems = gson.fromJson(json, type);
            return arrayItems;
        }else {
            return arrayItems;
        }
    }

    public static List<String> getList(Context context){
        List<String> arrayItems = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("KeyPairBoolData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("visitorBoolData", null);
        if (json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            arrayItems = gson.fromJson(json, type);
            return arrayItems;
        }else {
            return arrayItems;
        }
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

    @NonNull
    public static String readconditionCustomer(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("conditionCustomer", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("condition", null);
        if (json != null)
            return json;
        else {
             String conditionCustomer="بدون وضعیت";
            return conditionCustomer;
        }
    }


}
