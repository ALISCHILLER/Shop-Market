package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
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
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.VisitTemplatePathCustomer.VisitTemplatePathCustomer;
import com.varanegar.vaslibrary.model.VisitTemplatePathCustomer.VisitTemplatePathCustomerModel;
import com.varanegar.vaslibrary.model.VisitTemplatePathCustomer.VisitTemplatePathCustomerModelRepository;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.visitday.VisitTemplateCustomerModelApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */

public class VisitTemplatePathCustomerManager extends BaseManager<VisitTemplatePathCustomerModel> {
    @SubsystemType(id = SubsystemTypeId.Dist)
    public static UUID getDefaultDistributionPathId() {
        return UUID.fromString("a07d2ca1-b447-425b-9749-251138e5d0b6");
    }


    private Call<List<VisitTemplatePathCustomerModel>> call;

    public VisitTemplatePathCustomerManager(@NonNull Context context) {
        super(context, new VisitTemplatePathCustomerModelRepository());
    }

    public void sync(@Nullable final UUID customerId, @NonNull final UpdateCall updateCall) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            try {
                deleteAll();
                CustomerManager customerManager = new CustomerManager(getContext());
                List<CustomerModel> customers = customerManager.getAll();
                List<VisitTemplatePathCustomerModel> paths = new ArrayList<>();
                for (CustomerModel customer :
                        customers) {
                    VisitTemplatePathCustomerModel visitTemplatePathCustomerModel = new VisitTemplatePathCustomerModel();
                    visitTemplatePathCustomerModel.CustomerId = customer.UniqueId;
                    visitTemplatePathCustomerModel.PathRowId = customer.rowIndex;
                    visitTemplatePathCustomerModel.VisitTemplatePathId = getDefaultDistributionPathId();
                    visitTemplatePathCustomerModel.UniqueId = UUID.randomUUID();
                    paths.add(visitTemplatePathCustomerModel);
                }
                if (paths.size() > 0)
                    insert(paths);
                updateCall.success();
            } catch (DbException e) {
                updateCall.failure(getContext().getString(R.string.generating_visit_template_path_failed));
            } catch (ValidationException e) {
                updateCall.failure(getContext().getString(R.string.generating_visit_template_path_failed));
            }
        } else {
            UpdateManager updateManager = new UpdateManager(getContext());
            Date date = updateManager.getLog(UpdateKey.VisitTemplatePathCustomer);
            final String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
            VisitTemplateCustomerModelApi visitTemplateCustomerModelApi = new VisitTemplateCustomerModelApi(getContext());
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
            call = visitTemplateCustomerModelApi.getAll(dateString, UserManager.readFromFile(getContext()).UniqueId.toString(), settingsId.Value, customerId == null ? null : customerId.toString());
            visitTemplateCustomerModelApi.runWebRequest(call, new WebCallBack<List<VisitTemplatePathCustomerModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<VisitTemplatePathCustomerModel> result, Request request) {
                    if (customerId == null) {
                        try {
                            deleteAll();
                            if (result.size() > 0) {
                                try {
                                    insert(result);
                                    new UpdateManager(getContext()).addLog(UpdateKey.VisitTemplatePathCustomer);
                                    Timber.i("Updating visit template path customer completed");
                                    updateCall.success();
                                } catch (ValidationException e) {
                                    Timber.e(e);
                                    updateCall.failure(getContext().getString(R.string.data_validation_failed));
                                } catch (DbException e) {
                                    Timber.e(e);
                                    updateCall.failure(getContext().getString(R.string.data_error));
                                }
                            } else {
                                Timber.i("Updating visit template path customer completed. list was empty");
                                updateCall.success();
                            }
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
                        }
                    } else {
                        try {
                            insertOrUpdate(result);
                            new UpdateManager(getContext()).addLog(UpdateKey.VisitTemplatePathCustomer);
                            Timber.i("Updating visit template path customer completed");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
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

                @Override
                public void onCancel(Request request) {
                    super.onCancel(request);
                    updateCall.failure(getContext().getString(R.string.request_canceled));
                }
            });
        }

    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }

    public List<VisitTemplatePathCustomerModel> getCustomerPathUniqueId(UUID customerId) {
        Query query = new Query();
        query.from(VisitTemplatePathCustomer.VisitTemplatePathCustomerTbl).whereAnd(Criteria.equals(VisitTemplatePathCustomer.CustomerId, customerId.toString()));
        return getItems(query);
    }
}
