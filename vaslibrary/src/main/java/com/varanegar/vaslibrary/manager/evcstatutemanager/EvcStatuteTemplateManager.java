package com.varanegar.vaslibrary.manager.evcstatutemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductGroupModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteTemplate;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteTemplateModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteTemplateModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.EvcStatuteApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 11/20/2017.
 */

public class EvcStatuteTemplateManager extends BaseManager<EvcStatuteTemplateModel> {


    public EvcStatuteTemplateManager(@NonNull Context context) {
        super(context, new EvcStatuteTemplateModelRepository());
    }

    public void sync(final UpdateCall call) {
                try {
            deleteAll();
            save(call);
        } catch (DbException e) {
            Timber.e(e);
            call.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    private void save(final UpdateCall call) {

        EvcStatuteApi api = new EvcStatuteApi(getContext());
        api.runWebRequest(api.get(VaranegarApplication.getInstance().getAppId()), new WebCallBack<List<EvcStatuteTemplateModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final List<EvcStatuteTemplateModel> result, Request request) {
                if (result != null && result.size() > 0) {
                    try {
                        insert(result);
                        List<EvcStatuteProductModel> evcStatuteProductModels = new ArrayList<>();
                        for (EvcStatuteTemplateModel evcStatuteTemplateModel :
                                result) {
                            for (EvcStatuteProductModel evcStatuteProductModel :
                                    evcStatuteTemplateModel.EvcStatuteProducts) {
                                evcStatuteProductModel.TemplateId = evcStatuteTemplateModel.UniqueId;
                                evcStatuteProductModels.add(evcStatuteProductModel);
                            }
                        }
                        final List<EvcStatuteProductGroupModel> evcStatuteProductGroupModels = new ArrayList<>();
                        for (EvcStatuteTemplateModel evcStatuteTemplateModel :
                                result) {
                            for (EvcStatuteProductGroupModel evcStatuteProductGroupModel :
                                    evcStatuteTemplateModel.EvcStatuteProductGroups) {
                                evcStatuteProductGroupModel.TemplateId = evcStatuteTemplateModel.UniqueId;
                                evcStatuteProductGroupModels.add(evcStatuteProductGroupModel);
                            }
                        }
                        // inserting products
                        EvcStatuteProductManager evcStatuteProductManager = new EvcStatuteProductManager(getContext());
                        if (evcStatuteProductModels.size() > 0) {
                            evcStatuteProductManager.insert(evcStatuteProductModels);
                            insertProductGroups(evcStatuteProductGroupModels);
                        } else {
                            Timber.i("List of evc statues was empty");
                            insertProductGroups(evcStatuteProductGroupModels);
                        }
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("List of evc statues was empty");
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

    private void insertProductGroups(List<EvcStatuteProductGroupModel> evcStatuteProductGroupModels) throws ValidationException, DbException {
        // inserting product groups
        EvcStatuteProductGroupManager evcStatuteProductGroupManager = new EvcStatuteProductGroupManager(getContext());
        if (evcStatuteProductGroupModels.size() > 0) {
            evcStatuteProductGroupManager.insert(evcStatuteProductGroupModels);
        } else {
            Timber.i("List of evc statues was empty");
        }
    }

    @NonNull
    public List<EvcStatuteTemplateModel> getValidTemplatesForCustomer(@NonNull UUID customerId) {
        CustomerManager customerManager = new CustomerManager(getContext());
        CustomerModel customer = customerManager.getItem(customerId);
        if (customer == null)
            throw new NullPointerException("CustomerId is not valid");
        String saleOfficeId = SysConfigManager.getValue(new SysConfigManager(getContext()).read(ConfigKey.SaleOfficeId, SysConfigManager.cloud));
        Query query = new Query();
        query.from(EvcStatuteTemplate.EvcStatuteTemplateTbl)
                .whereAnd(Criteria.contains(EvcStatuteTemplate.CenterUniqueIds, customer.CenterId == null ? UUID.randomUUID().toString() : customer.CenterId.toString())
                        .or(Criteria.isNull(EvcStatuteTemplate.CenterUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.CenterUniqueIds, "")))
                .whereAnd(Criteria.contains(EvcStatuteTemplate.CityUniqueIds, customer.CityId == null ? UUID.randomUUID().toString() : customer.CityId.toString())
                        .or(Criteria.isNull(EvcStatuteTemplate.CityUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.CityUniqueIds, "")))
                .whereAnd(Criteria.contains(EvcStatuteTemplate.CustomerActivityUniqueIds, customer.CustomerActivityId == null ? UUID.randomUUID().toString() : customer.CustomerActivityId.toString())
                        .or(Criteria.isNull(EvcStatuteTemplate.CustomerActivityUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.CustomerActivityUniqueIds, "")))
                .whereAnd(Criteria.contains(EvcStatuteTemplate.CustomerCategoryUniqueIds, customer.CustomerCategoryId == null ? UUID.randomUUID().toString() : customer.CustomerCategoryId.toString())
                        .or(Criteria.isNull(EvcStatuteTemplate.CustomerCategoryUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.CustomerCategoryUniqueIds, "")))
                .whereAnd(Criteria.contains(EvcStatuteTemplate.CustomerLevelUniqueIds, customer.CustomerLevelId == null ? UUID.randomUUID().toString() : customer.CustomerLevelId.toString())
                        .or(Criteria.isNull(EvcStatuteTemplate.CustomerLevelUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.CustomerLevelUniqueIds, "")))
                .whereAnd(Criteria.contains(EvcStatuteTemplate.SaleOfficeUniqueIds, saleOfficeId == null ? UUID.randomUUID().toString() : saleOfficeId)
                        .or(Criteria.isNull(EvcStatuteTemplate.SaleOfficeUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.SaleOfficeUniqueIds, "")))
                .whereAnd(Criteria.contains(EvcStatuteTemplate.SaleZoneUniqueIds, customer.AreaId == null ? UUID.randomUUID().toString() : customer.AreaId.toString())
                        .or(Criteria.isNull(EvcStatuteTemplate.SaleZoneUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.SaleZoneUniqueIds, "")))
                .whereAnd(Criteria.contains(EvcStatuteTemplate.StateUniqueIds, customer.StateId == null ? UUID.randomUUID().toString() : customer.StateId.toString())
                        .or(Criteria.isNull(EvcStatuteTemplate.StateUniqueIds))
                        .or(Criteria.equals(EvcStatuteTemplate.StateUniqueIds, "")))
                .whereAnd(Criteria.lesserThan(EvcStatuteTemplate.FromDate, new Date())
                        .or(Criteria.isNull(EvcStatuteTemplate.FromDate)))
                .whereAnd(Criteria.greaterThan(EvcStatuteTemplate.ToDate, new Date())
                        .or(Criteria.isNull(EvcStatuteTemplate.ToDate)));
        return getItems(query);
    }

}
