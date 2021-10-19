package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.disacc.DisAccModel;
import com.varanegar.vaslibrary.model.disacc.DisAccModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.disacc.DisAccApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public class DisAccManager extends BaseManager<DisAccModel> {
    private Call<List<DisAccModel>> call;

    public DisAccManager(@NonNull Context context) {
        super(context, new DisAccModelRepository());
    }

    public void save(final UpdateCall updateCall) {
        DisAccApi disAccApi = new DisAccApi(getContext());
        call = disAccApi.getDisAcc();
        disAccApi.runWebRequest(call, new WebCallBack<List<DisAccModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<DisAccModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.DisAcc);
                        Timber.i(result.size() + " rows DisAcc updated");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating DisAcc, DisAcc list was empty");
                    updateCall.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t.getMessage());
                updateCall.failure(getContext().getString(R.string.network_error));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.tour_canceled));
            }

        });
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        try {
            deleteAll();
            save(updateCall);
        } catch (DbException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    public  void  cancel(){
        if (call != null && !call.isCanceled())
            call.cancel();
    }
}
