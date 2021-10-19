package com.varanegar.vaslibrary.manager.visitday;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.VisitTemplatePathCustomerManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.visitday.VisitDay;
import com.varanegar.vaslibrary.model.visitday.VisitDayModel;
import com.varanegar.vaslibrary.model.visitday.VisitDayModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.visitday.VisitDayApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/27/2017.
 */

public class VisitDayManager extends BaseManager<VisitDayModel> {
    public VisitDayManager(@NonNull Context context) {
        super(context, new VisitDayModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            VisitDayModel visitDayModel = new VisitDayModel();
            visitDayModel.PathTitle = getContext().getString(R.string.day_customer);
            visitDayModel.RowIndex = 0;
            visitDayModel.UniqueId = VisitTemplatePathCustomerManager.getDefaultDistributionPathId();
            try {
                deleteAll();
                insert(visitDayModel);
                updateCall.success();
            } catch (Exception e) {
                Timber.e(e);
                updateCall.failure(getContext().getString(R.string.error_saving_request));
            }
            return;
        }
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.VisitDay);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        VisitDayApi visitDayApi = new VisitDayApi(getContext());
        visitDayApi.runWebRequest(visitDayApi.getAll(dateString, UserManager.readFromFile(getContext()).UniqueId.toString()), new WebCallBack<List<VisitDayModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<VisitDayModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.VisitDay);
                        Timber.i("Updating visitday completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("visit day list was empty");
                    updateCall.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            public void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });

    }

    public static Query getAll() {
        Query query = new Query();
        query.from(VisitDay.VisitDayTbl).orderByAscending(VisitDay.RowIndex);
        return query;
    }

}
