package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.county.County;
import com.varanegar.vaslibrary.model.county.CountyModel;
import com.varanegar.vaslibrary.model.county.CountyModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.county.CountyApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public class CountyManager extends BaseManager<CountyModel> {
    public CountyManager(@NonNull Context context) {
        super(context, new CountyModelRepository());
    }

    public void sync(@NonNull final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.County);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        CountyApi countyApi = new CountyApi(getContext());
        countyApi.runWebRequest(countyApi.getCounties(dateString), new WebCallBack<List<CountyModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CountyModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.County);
                        Timber.i("Updating counties completed");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating county completed. List of city was empty");
                    call.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public List<CountyModel> getAll() {
        return getItems(new Query().from(County.CountyTbl));
    }
}
