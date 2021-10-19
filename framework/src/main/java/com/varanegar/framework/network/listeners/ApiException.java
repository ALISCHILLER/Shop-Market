package com.varanegar.framework.network.listeners;

import androidx.annotation.Nullable;

import okhttp3.Request;

/**
 * Created by atp on 8/18/2016.
 */
public class ApiException extends Exception{
    public ApiError getApiError() {
        return apiError;
    }

    public Request getRequest() {
        return request;
    }

    private final ApiError apiError;
    private final Request request;

    public ApiException(ApiError apiError, Request request){
        this.apiError = apiError;
        this.request = request;
    }
}

