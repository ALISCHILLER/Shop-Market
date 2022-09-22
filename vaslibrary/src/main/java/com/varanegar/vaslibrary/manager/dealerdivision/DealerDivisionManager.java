package com.varanegar.vaslibrary.manager.dealerdivision;

import android.content.Context;
import android.util.Log;

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
import com.varanegar.vaslibrary.model.dealerdivision.DealerDivision;
import com.varanegar.vaslibrary.model.dealerdivision.DealerDivisionModel;
import com.varanegar.vaslibrary.model.dealerdivision.DealerDivisionModelRepository;
import com.varanegar.vaslibrary.model.user.User;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.dealerdivistion.DealerDivisionApi;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Create By Mehrdad Latifi on 9/21/2022
 */

public class DealerDivisionManager extends BaseManager<DealerDivisionModel> {


    //---------------------------------------------------------------------------------------------- DealerDivisionManager
    public DealerDivisionManager(@NonNull Context context) {
        super(context, new DealerDivisionModelRepository());
    }
    //---------------------------------------------------------------------------------------------- DealerDivisionManager


    //---------------------------------------------------------------------------------------------- sync
    public void sync(final UpdateCall call) {
        try {
            deleteAll();
            DealerDivisionApi api = new DealerDivisionApi(getContext());
            api.runWebRequest(api.get(), new WebCallBack<DealerDivisionModel>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(DealerDivisionModel result, Request request) {
                    if (result == null) {
                        Timber.i("DealerDivisionModel was empty");
                        call.failure("DealerDivisionModel was empty");
                    } else {
                        try {
                            result.UniqueId = result.DivisionCenterKey;
                            insert(result);
                            call.success();
                        } catch (ValidationException e) {
                            e.printStackTrace();
                            Timber.i("DealerDivisionModel ValidationException " + e.getMessage());
                        } catch (DbException e) {
                            e.printStackTrace();
                            Timber.i("DealerDivisionModel DbException " + e.getMessage());
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
    //---------------------------------------------------------------------------------------------- sync


    //---------------------------------------------------------------------------------------------- getDealerDivisionModel
    public DealerDivisionModel getDealerDivisionModel() {
        Query query = new Query();
        query.from(DealerDivision.DealerDivisionTbl);
        return getItem(query);
    }
    //---------------------------------------------------------------------------------------------- getDealerDivisionModel

}
