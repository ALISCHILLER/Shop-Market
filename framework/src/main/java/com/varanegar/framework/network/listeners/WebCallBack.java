package com.varanegar.framework.network.listeners;


import okhttp3.Request;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by atp on 8/18/2016.
 */
public abstract class WebCallBack<T> {
    public void setResponse(Response<T> response) {
        this.response = response;
    }

    protected Response<T> getResponse() {
        return response;
    }

    private Response<T> response;

    public void success(T result, Request request) {
        onFinish();
        onSuccess(result, request);
    }

    public void apiFailure(ApiError error, Request request) {
        onFinish();
        onApiFailure(error, request);
    }

    public void netWorkFailure(Throwable t, Request request) {
        onFinish();
        onNetworkFailure(t, request);
    }

    protected abstract void onFinish();

    protected abstract void onSuccess(T result, Request request);

    protected abstract void onApiFailure(ApiError error, Request request);

    protected abstract void onNetworkFailure(Throwable t, Request request);

    public void onCancel(Request request) {
        Timber.d("Request " + request.url().toString() + " canceled!");
    }
}
