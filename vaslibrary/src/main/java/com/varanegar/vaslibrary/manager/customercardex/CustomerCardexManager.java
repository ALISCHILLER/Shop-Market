package com.varanegar.vaslibrary.manager.customercardex;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardex;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexModel;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexModelRepository;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexTempModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerCardexApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 9/11/2017.
 */

public class CustomerCardexManager extends BaseManager<CustomerCardexModel> {
    private Call<List<CustomerCardexModel>> call;

    public CustomerCardexManager(@NonNull Context context) {
        super(context, new CustomerCardexModelRepository());
    }

    public CustomerCardexCreditModel getCustomerValidSumDebAndCredit(UUID CustomerId) {
        Query query = new Query();
        query.from(CustomerCardex.CustomerCardexTbl)
                .whereAnd(Criteria.equals(CustomerCardex.CustomerUniqueId, CustomerId)
                        .and(Criteria.lesserThan(CustomerCardex.NotDueDateMiladi, new Date())))
                .select(Projection.sum(CustomerCardex.BedAmount).as("BedAmount"), Projection.sum(CustomerCardex.BesAmount).as("BesAmount"));
        CustomerCardexCreditModel customerCardexCreditModel = new CustomerCardexCreditModelRepository().getItem(query);
        return customerCardexCreditModel;
    }

    public boolean checkHasDouDate(UUID customerId) {
        Query query = new Query();
        query.from(CustomerCardex.CustomerCardexTbl).whereAnd(Criteria.equals(CustomerCardex.CustomerUniqueId, customerId.toString()))
                .whereAnd(Criteria.isNull(CustomerCardex.NotDueDate));
        List<CustomerCardexModel> customerCardexModels = getItems(query);
        if (customerCardexModels.size() == 0)
            return false;
        else
            return true;
    }

    public void sync(@Nullable final UUID customerId, @Nullable final Date startDate, Date endDate, final UpdateCall updateCall) {
        try {
            UpdateManager updateManager = new UpdateManager(getContext());
            Date date = updateManager.getLog(UpdateKey.CustomerCardex);
            String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
            final CustomerCardexApi customerCardexApi = new CustomerCardexApi(getContext());
            TourManager tourManager = new TourManager(getContext());
            TourModel tourModel = tourManager.loadTour();
            if (tourModel == null || tourModel.UniqueId == null) {
                updateCall.failure(getContext().getString(R.string.tour_is_not_available));
                return;
            }
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
            if (sysConfigModel == null) {
                updateCall.failure(getContext().getString(R.string.settings_id_not_found));
                return;
            }

            call = customerCardexApi.getAll(VaranegarApplication.getInstance().getAppId().toString(),
                    tourModel.UniqueId.toString(),
                    dateString,
                    sysConfigModel.Value,
                    customerId == null ? null : customerId.toString(),
                    DateHelper.toString(startDate, DateFormat.MicrosoftDateTime, Locale.US),
                    DateHelper.toString(endDate, DateFormat.MicrosoftDateTime, Locale.US));

            customerCardexApi.runWebRequest(call, new WebCallBack<List<CustomerCardexModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<CustomerCardexModel> result, Request request) {
                    try {
                        if (result.size() == 0) {
                            if (startDate != null) {
                                CustomerCardexTempModel customerCardexTempModel = new CustomerCardexTempModel();
                                customerCardexTempModel.UniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");
                                customerCardexTempModel.CustomerUniqueId = customerId;
                                CustomerCardexTempManager customerCardexTempManager = new CustomerCardexTempManager(getContext());
                                customerCardexTempManager.deleteAll();
                                customerCardexTempManager.insert(customerCardexTempModel);
                            } else
                                deleteAll();
                            Timber.i("Customer Cardex is empty");
                        } else {
                            if (startDate != null) {
                                List<CustomerCardexTempModel> customerCardexTempModels = new ArrayList<>();
                                for (CustomerCardexModel customerCardexModel : result) {
                                    CustomerCardexTempModel customerCardexTempModel = customerCardexModel.convert();
                                    customerCardexTempModels.add(customerCardexTempModel);
                                }
                                CustomerCardexTempManager customerCardexTempManager = new CustomerCardexTempManager(getContext());
                                customerCardexTempManager.deleteAll();
                                customerCardexTempManager.insert(customerCardexTempModels);
                                Timber.i("Updating customer cardex temp completed");
                            } else {
                                deleteAll();
                                insert(result);
                                new UpdateManager(getContext()).addLog(UpdateKey.CustomerCardex);
                                Timber.i("Updating customer cardex completed");
                            }
                        }
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.error_saving_request));
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, getContext());
                    updateCall.failure(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t, "http request failed: " + request.url());
                    updateCall.failure(getContext().getString(R.string.network_error));
                }

                @Override
                public void onCancel(Request request) {
                    super.onCancel(request);
                    updateCall.failure(getContext().getString(R.string.request_canceled));
                }
            });
        } catch (Exception e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    public static Query getAll(UUID key, boolean orderBy) {
        Query query = new Query();
        query.from(CustomerCardex.CustomerCardexTbl).whereAnd(Criteria.equals(CustomerCardex.CustomerUniqueId, key));
        if (orderBy)
            query.orderByAscending(CustomerCardex.SortId);
        return query;
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }
}
