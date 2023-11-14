package com.varanegar.vaslibrary.webapi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.varanegar.framework.network.BadRequestModel;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.vaslibrary.R;

import timber.log.Timber;

/**
 * Created by A.Torabi on 9/2/2017.
 */

public class WebApiErrorBody {
    @NonNull
    public static String log(ApiError error, Context context) {
        if (error.getStatusCode() == 400) {
            WebApiErrorBody webApiErrorBody = error.errorBody(WebApiErrorBody.class);
            if (webApiErrorBody != null) {
                Timber.e(error.url() + " - " + error.getMessage() + " - " + error.getStatusCode() + " \n log message: " + webApiErrorBody.ErrLogMessage + " \n message: " + webApiErrorBody.ErrMessage);
                return webApiErrorBody.ErrMessage;
            } else {
                try {
                    Gson gson = new GsonBuilder().create();
                    BadRequestModel badRequestModel = gson.fromJson(error.errorBody(), BadRequestModel.class);
                    Timber.e(error.url() + " - " + error.getMessage() + " - " + error.getStatusCode() + " :\n " + badRequestModel.message);
                    return badRequestModel.message;
                } catch (Exception ex) {
                    Timber.e(error.url() + " - " + error.getMessage() + " - " + error.getStatusCode());
                    return context.getString(R.string.web_api_failure);
                }
            }
        } else if (error.getStatusCode() == 403){
            Timber.e(error.url() + " - " + error.getMessage() + " - " + error.getStatusCode() + " :\n " + error.errorBody());
            Log.e("403 do_not_access", "403: "+context.getString(R.string.do_not_access)+error.getMessage()+error.getStatusCode());
            return context.getString(R.string.do_not_access);
        } else {
            Timber.e(error.url() + " - " + error.getMessage() + " - " + error.getStatusCode() + " :\n " + error.errorBody());
            return context.getString(R.string.web_api_failure);
        }
    }

    public String ErrMessage;
    public String ErrCode;
    public String ErrLogMessage;
}
