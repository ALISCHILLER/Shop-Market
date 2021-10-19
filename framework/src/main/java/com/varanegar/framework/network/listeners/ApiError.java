package com.varanegar.framework.network.listeners;

import androidx.annotation.Nullable;

/**
 * Created by atp on 8/18/2016.
 */
public interface ApiError {
    int getStatusCode();
    String url();
    String getMessage();
    @Nullable
    String errorBody();
    @Nullable
    <T> T errorBody(Class<T> clazz);
}

