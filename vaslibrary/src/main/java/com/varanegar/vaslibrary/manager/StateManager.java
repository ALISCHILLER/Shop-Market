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
import com.varanegar.vaslibrary.model.state.State;
import com.varanegar.vaslibrary.model.state.StateModel;
import com.varanegar.vaslibrary.model.state.StateModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.state.StateApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public class StateManager extends BaseManager<StateModel> {
    public StateManager(@NonNull Context context) {
        super(context, new StateModelRepository());
    }

    public void sync(@NonNull final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.State);
        String dateStrin = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        StateApi api = new StateApi(getContext());
        api.runWebRequest(api.getStates(dateStrin), new WebCallBack<List<StateModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<StateModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.State);
                        Timber.i("Updating states completed");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating state completed. List of city was empty");
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

    public List<StateModel> getAll() {
        return getItems(new Query().from(State.StateTbl).orderByAscending(State.StateName));
    }
}
