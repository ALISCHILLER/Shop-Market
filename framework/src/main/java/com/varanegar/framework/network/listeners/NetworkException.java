package com.varanegar.framework.network.listeners;

import okhttp3.Request;

/**
 * Created by atp on 8/18/2016.
 */
public class NetworkException extends Exception {

    @Override
    public Throwable getCause() {
        return cause;
    }

    private final Throwable cause;

    public Request getRequest() {
        return request;
    }

    private final Request request;

    public NetworkException(Throwable cause, Request request) {
        this.request = request;
        this.cause = cause;
    }
}

