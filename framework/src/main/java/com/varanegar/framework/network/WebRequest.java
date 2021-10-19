package com.varanegar.framework.network;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.ApiException;
import com.varanegar.framework.network.listeners.InterruptedException;
import com.varanegar.framework.network.listeners.NetworkException;
import com.varanegar.framework.network.listeners.WebCallBack;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by atp on 12/19/2016.
 */
public abstract class WebRequest {
    public <T> void runWebRequest(Call<T> call, @Nullable final WebCallBack<T> callBack) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, final Response<T> response) {
                if (callBack != null)
                    callBack.setResponse(response);
                if (response.isSuccessful()) {
                    Timber.v("web request success: " + call.request().toString());
                    if (callBack != null)
                        callBack.success(response.body(), response.raw().request());
                } else {
                    String errorBody = null;
                    String message = null;
                    final String url = response.raw().request().url().toString();
                    try {
                        errorBody = response.errorBody().string();
                        message = response.message();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final String finalMessage = message;
                    final String finalErrorBody = errorBody;
                    Timber.v("web request api failure error code " + response.code() + ": " + call.request().toString());
                    if (callBack != null)
                        callBack.apiFailure(new ApiError() {
                            @Override
                            public int getStatusCode() {
                                return response.code();
                            }

                            @Override
                            public String getMessage() {
                                return finalMessage;
                            }

                            @Override
                            public String errorBody() {
                                try {
                                    return finalErrorBody;
                                } catch (Exception e) {
                                    return null;
                                }
                            }

                            @Nullable
                            @Override
                            public <T> T errorBody(Class<T> clazz) {
                                Gson gson = new GsonBuilder().create();
                                try {
                                    BadRequestModel badRequestModel = gson.fromJson(finalErrorBody, BadRequestModel.class);
                                    return gson.fromJson(badRequestModel.message, clazz);
                                } catch (Exception e) {
                                    return null;
                                }
                            }

                            @Override
                            public String url() {
                                return url;
                            }
                        }, response.raw().request());
                }
            }

            @Override
            public void onFailure(final Call<T> call, final Throwable t) {
                if (call.isCanceled()) {
                    if (callBack != null)
                        callBack.onCancel(call.request());
                    Timber.v("web request canceled: " + call.request().toString());
                } else if (t instanceof com.google.gson.JsonSyntaxException) {
                    Timber.v("web request json syntax error: " + call.request().toString());
                    if (callBack != null)
                        callBack.apiFailure(new ApiError() {
                            @Override
                            public int getStatusCode() {
                                return 520;
                            }

                            @Override
                            public String getMessage() {
                                return "Json Syntax Exception: " + t.getCause().toString();
                            }

                            @Override
                            public String errorBody() {
                                return null;
                            }

                            @Nullable
                            @Override
                            public <T> T errorBody(Class<T> clazz) {
                                return null;
                            }

                            @Override
                            public String url() {
                                return call.request().url().toString();
                            }
                        }, call.request());
                } else {
                    Timber.v("web request network failure: " + call.request().toString());
                    if (callBack != null)
                        callBack.netWorkFailure(t, call.request());
                }
            }
        });
    }

    /**
     * @param call
     * @param <T>
     * @return Result of type T
     * @throws ApiException
     * @throws NetworkException
     * @throws InterruptedException if someone cancels the request this runtime exception will be thrown
     */
    public <T> T runWebRequest(final Call<T> call) throws ApiException, NetworkException, InterruptedException {
        try {
            Timber.v(call.request().toString());
            final Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                String errorBody = null;
                String message = null;
                final String url = response.raw().request().url().toString();
                try {
                    errorBody = response.errorBody().string();
                    message = response.message();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String finalMessage = message;
                final String finalErrorBody = errorBody;
                throw new ApiException(new ApiError() {
                    @Override
                    public int getStatusCode() {
                        return response.code();
                    }

                    @Override
                    public String getMessage() {
                        return finalMessage;
                    }

                    @Override
                    public String errorBody() {
                        try {
                            return finalErrorBody;
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Nullable
                    @Override
                    public <T> T errorBody(Class<T> clazz) {
                        Gson gson = new GsonBuilder().create();
                        try {
                            BadRequestModel badRequestModel = gson.fromJson(finalErrorBody, BadRequestModel.class);
                            return gson.fromJson(badRequestModel.message, clazz);
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public String url() {
                        return url;
                    }
                }, response.raw().request());
            }
        } catch (IOException ex) {
            if (ex instanceof java.io.InterruptedIOException) {
                Timber.d("Request " + call.request().url().toString() + " canceled!");
                throw new InterruptedException(ex, call.request());
            }
            Timber.e(ex);
            throw new NetworkException(ex, call.request());
        } catch (final Throwable t) {
            if (t instanceof IllegalStateException) {
                throw new ApiException(new ApiError() {
                    @Override
                    public int getStatusCode() {
                        return 520;
                    }

                    @Override
                    public String getMessage() {
                        return "Illegal state Exception " + t.toString();
                    }

                    @Override
                    public String errorBody() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public <T> T errorBody(Class<T> clazz) {
                        return null;
                    }

                    @Override
                    public String url() {
                        return call.request().url().toString();
                    }
                }, call.request());
            } else if (t instanceof com.google.gson.JsonSyntaxException)
                throw new ApiException(new ApiError() {
                    @Override
                    public int getStatusCode() {
                        return 520;
                    }

                    @Override
                    public String getMessage() {
                        return "Json Syntax Exception: " + t.getCause().toString();
                    }

                    @Override
                    public String errorBody() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public <T> T errorBody(Class<T> clazz) {
                        return null;
                    }

                    @Override
                    public String url() {
                        return call.request().url().toString();
                    }
                }, call.request());
            else {
                Timber.e(t);
                throw new NetworkException(t, call.request());
            }
        }

    }
}
