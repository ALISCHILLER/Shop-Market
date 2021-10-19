package com.varanegar.vaslibrary.webapi.timezone;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class TimeApi extends BaseApi implements ITimeApi {
    public TimeApi(Context context) {
        super(context);
    }

    @Override
    public Call<TimeViewModel> getTimeZone() {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ITimeApi.class).getTimeZone();
    }

    public interface ICheckTimeCallBack {
        void onError(String message);
    }

    public void checkTime(@NonNull ICheckTimeCallBack callBack) {
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                TimeApi timeApi = new TimeApi(getContext());
                timeApi.runWebRequest(timeApi.getTimeZone(), new WebCallBack<TimeViewModel>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(TimeViewModel result, Request request) {
                        TimeZone timeZone = Calendar.getInstance().getTimeZone();
                        if (timeZone.getRawOffset() - result.TimeZone != 0) {
                            callBack.onError("Time zone is different: device time zone = " + timeZone.getRawOffset() + " server time zone = " + result.TimeZone);
                        } else {
                            Date systemDate = new Date();
                            if (Math.abs(systemDate.getTime() - result.Date.getTime()) >= 1800 * 1000)
                                callBack.onError("Tablet time and server time is different.  Tablet time = " +
                                        DateHelper.toString(systemDate, DateFormat.Complete, Locale.getDefault()) +
                                        "server time = " + DateHelper.toString(result.Date, DateFormat.Complete, Locale.getDefault()));
                        }
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        WebApiErrorBody.log(error, getContext());
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Timber.e(t);
                    }
                });
            }

            @Override
            public void failed() {

            }
        });
    }
}
