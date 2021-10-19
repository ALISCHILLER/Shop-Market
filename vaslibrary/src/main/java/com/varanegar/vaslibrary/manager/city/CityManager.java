package com.varanegar.vaslibrary.manager.city;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.city.City;
import com.varanegar.vaslibrary.model.city.CityModel;
import com.varanegar.vaslibrary.model.city.CityModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.city.CityApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 3/25/2018.
 */

public class CityManager extends BaseManager<CityModel> {
    public CityManager(@NonNull Context context) {
        super(context, new CityModelRepository());
    }

    public void sync(final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.City);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        CityApi cityApi = new CityApi(getContext());
        cityApi.runWebRequest(cityApi.getCities(dateString), new WebCallBack<List<CityModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CityModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.City);
                        Timber.i("Updating cities completed");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating city completed. List of city was empty");
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

    public List<CityModel> getAllCities() {
        Query query = new Query();
        query.from(City.CityTbl).orderByAscending(City.CityName);
        return getItems(query);
    }

    public List<CityModel> getSatesCities(UUID stateUniqueId) {
        Query query = new Query();
        query.from(City.CityTbl).whereAnd(Criteria.equals(City.StateUniqueId, stateUniqueId)).orderByAscending(City.CityName);
        return getItems(query);
    }

}
