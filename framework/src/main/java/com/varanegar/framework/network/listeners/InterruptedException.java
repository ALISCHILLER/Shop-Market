package com.varanegar.framework.network.listeners;

import okhttp3.Request;

/**
 * Created by A.Torabi on 12/16/2018.
 */

public class InterruptedException extends Exception {
    public Request getRequest() {
        return request;
    }

    private final Request request;

    @Override
    public Throwable getCause() {
        return cause;
    }

    private final Throwable cause;

    public InterruptedException(Throwable cause, Request request) {
        this.cause = cause;
        this.request = request;
    }
}
