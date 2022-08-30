package com.varanegar.vaslibrary.manager.msl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerLevel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModelRepository;
import com.varanegar.vaslibrary.model.msl.Msl;
import com.varanegar.vaslibrary.model.msl.MslModel;
import com.varanegar.vaslibrary.model.msl.MslModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 1/28/2018.
 */

public class MslManager extends BaseManager<MslModel> {
    public MslManager(@NonNull Context context) {
        super(context, new MslModelRepository());
    }


    public void insertTest() {
        MslModel mslModel = new MslModel(UUID.fromString("343fe2c6-15bf-4a7b-aac0-9fab87737cbc"),UUID.fromString("3ae3d8e8-30cb-43d0-94b6-4a861806752b"), true);
        MslModel mslModel1 = new MslModel(UUID.fromString("343fe2c6-15bf-4a7b-aac0-9fab87737cbc"),UUID.fromString("5f3e80dc-f518-47f7-9e18-2793de4fb5d2"), false);
        MslModel mslModel2 = new MslModel(UUID.fromString("343fe2c6-15bf-4a7b-aac0-9fab87737cbc"),UUID.fromString("b93e6f65-f499-4b88-95e7-bb9008d89e89"), false);
        mslModel.UniqueId = UUID.fromString("343fe2c6-15bf-4a7b-aac0-9fab87737001");
        mslModel1.UniqueId = UUID.fromString("343fe2c6-15bf-4a7b-aac0-9fab87737002");
        mslModel2.UniqueId = UUID.fromString("343fe2c6-15bf-4a7b-aac0-9fab87737003");

        try {
            insert(mslModel);
            insert(mslModel1);
            insert(mslModel2);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


/*
    public void sync(final UpdateCall call) {
        try {
            deleteAll();
            CustomerApi api = new CustomerApi(getContext());
            api.runWebRequest(api.getCustomerLevels(), new WebCallBack<List<CustomerLevelModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<CustomerLevelModel> result, Request request) {
                    if (result.size() == 0) {
                        Timber.i("List of customer levels was empty");
                        call.success();
                    } else {
                        Timber.i("List of customer levels has " + result.size() + " rows.");
                        try {
                            long affectedRows = insert(result);
                            Timber.i(affectedRows + " rows inserted in to table customer levels");
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_error));
                        }
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
        } catch (DbException e) {
            Timber.e(e);
            call.failure(getContext().getString(R.string.error_deleting_old_data));
        }

    }
*/

    public List<MslModel> getAll(String CustomerLevelId) {
        return getItems(new Query().from(Msl.MslTbl).whereAnd(Criteria.equals(Msl.CustomerLevelId, CustomerLevelId)));
    }
}
