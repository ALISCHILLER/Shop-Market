package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.dataforregister.DataForRegister;
import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;
import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.dataforregister.DataForRegisterApi;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

// NGT-4023 زر ماکارون
public class DataForRegisterManager extends BaseManager<DataForRegisterModel> {


    public DataForRegisterManager(@NonNull Context context) {
        super(context, new DataForRegisterModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        DataForRegisterApi api = new DataForRegisterApi(getContext());
        api.runWebRequest(api.getAll(), new WebCallBack<List<DataForRegisterModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<DataForRegisterModel> result, Request request) {
                try {
                    deleteAll();
                    if (result.size() > 0) {
                        try {
                            for (DataForRegisterModel data :
                                    result) {
                                data.UniqueId = UUID.randomUUID();
                            }
                            insert(result);
                            Timber.i("Updating list of Data register for zar makaron");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("List of Data register for zar makaron was empty");
                        updateCall.success();
                    }
                } catch (DbException e) {
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public List<DataForRegisterModel> getAll() {
        return getItems(new Query().from(DataForRegister.DataForRegisterTbl));
    }
}
