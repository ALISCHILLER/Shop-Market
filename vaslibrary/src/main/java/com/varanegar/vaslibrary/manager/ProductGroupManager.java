package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.JoinFrom;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.productGroup.ProductGroup;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.product.ProductGroupApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/14/2017.
 */

public class ProductGroupManager extends BaseManager<ProductGroupModel> {
    private Call<List<ProductGroupModel>> call;

    public ProductGroupManager(@NonNull Context context) {
        super(context, new ProductGroupModelRepository());
    }

    public static Query getSubGroups(UUID groupId, ProductType productType) {
        Query query = new Query();
        if (productType == ProductType.isForSale) {
            query.select(ProductGroup.ProductGroupAll);
            query.from(JoinFrom.table(ProductGroup.ProductGroupTbl)
                    .innerJoin(Product.ProductTbl)
                    .on(Product.ProductGroupId, ProductGroup.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForSale, true)))
                    .groupBy(ProductGroup.UniqueId);
        } else if (productType == ProductType.isForReturn) {
            query.select(ProductGroup.ProductGroupAll);
            query.from(JoinFrom.table(ProductGroup.ProductGroupTbl)
                    .innerJoin(Product.ProductTbl)
                    .on(Product.ProductGroupId, ProductGroup.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForReturnWithRef, true)
                            .or(Criteria.equals(Product.IsForReturnWithOutRef, true))))
                    .groupBy(ProductGroup.UniqueId);
        } else if (productType == ProductType.isForRequest) {
            query.select(ProductGroup.ProductGroupAll);
            query.from(JoinFrom.table(ProductGroup.ProductGroupTbl)
                    .innerJoin(Product.ProductTbl)
                    .on(Product.ProductGroupId, ProductGroup.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForRequest, true)))
                    .groupBy(ProductGroup.UniqueId);
        } else {
            query.from(ProductGroup.ProductGroupTbl);
        }
        query.whereAnd(Criteria.equals(ProductGroup.ProductGroupParentId, groupId.toString()))
                .orderByAscending(ProductGroup.OrderOf);
        return query;
    }

    @NonNull
    public UUID[] getSubGroupIds(UUID groupId, ProductType productType) {
        Query query = getSubGroups(groupId, productType);
        List<ProductGroupModel> groups = getItems(query);
        UUID[] ids = new UUID[groups.size()];
        for (int i = 0; i < groups.size(); i++)
            ids[i] = (groups.get(i).UniqueId);
        return ids;
    }
    public List<ProductGroupModel> getAll(){
        return getItems(new Query().from(ProductGroup.ProductGroupTbl));
    }
    public List<ProductGroupModel> getParentItems(ProductType type) {
        Query query = new Query();
        if (type == ProductType.isForSale) {
            Query q2 = new Query().from(JoinFrom.table(ProductGroup.ProductGroupTbl)
                    .innerJoin(Product.ProductTbl)
                    .on(Product.ProductGroupId, ProductGroup.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForSale, true)))
                    .groupBy(ProductGroup.UniqueId);
            List<ProductGroupModel> g = getItems(q2);
            List<String> parentIds = Linq.map(g, new Linq.Map<ProductGroupModel, String>() {
                @Override
                public String run(ProductGroupModel item) {
                    return item.ProductGroupParentId == null ? item.UniqueId.toString() : item.ProductGroupParentId.toString();
                }
            });
            query.select(ProductGroup.ProductGroupAll);
            query.from(ProductGroup.ProductGroupTbl)
                    .whereAnd(Criteria.in(ProductGroup.UniqueId, parentIds));
        } else if (type == ProductType.isForReturn) {
            Query q2 = new Query().from(JoinFrom.table(ProductGroup.ProductGroupTbl)
                    .innerJoin(Product.ProductTbl)
                    .on(Product.ProductGroupId, ProductGroup.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForReturnWithOutRef, true)
                            .or(Criteria.equals(Product.IsForReturnWithRef, true))))
                    .groupBy(ProductGroup.UniqueId);
            List<ProductGroupModel> g = getItems(q2);
            List<String> parentIds = Linq.map(g, new Linq.Map<ProductGroupModel, String>() {
                @Override
                public String run(ProductGroupModel item) {
                    return item.ProductGroupParentId == null ? item.UniqueId.toString() : item.ProductGroupParentId.toString();
                }
            });
            query.select(ProductGroup.ProductGroupAll);
            query.from(ProductGroup.ProductGroupTbl)
                    .whereAnd(Criteria.in(ProductGroup.UniqueId, parentIds));
        } else if (type == ProductType.isForRequest) {
            Query q2 = new Query().from(JoinFrom.table(ProductGroup.ProductGroupTbl)
                    .innerJoin(Product.ProductTbl)
                    .on(Product.ProductGroupId, ProductGroup.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForRequest, true)))
                    .groupBy(ProductGroup.UniqueId);
            List<ProductGroupModel> g = getItems(q2);
            List<String> parentIds = Linq.map(g, new Linq.Map<ProductGroupModel, String>() {
                @Override
                public String run(ProductGroupModel item) {
                    return item.ProductGroupParentId == null ? item.UniqueId.toString() : item.ProductGroupParentId.toString();
                }
            });
            query.select(ProductGroup.ProductGroupAll);
            query.from(ProductGroup.ProductGroupTbl)
                    .whereAnd(Criteria.in(ProductGroup.UniqueId, parentIds));
        } else
            query.from(ProductGroup.ProductGroupTbl).whereAnd(Criteria.equals(ProductGroup.ProductGroupParentId, null));
        return getItems(query);
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.ProductGroup);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        ProductGroupApi productGroupApi = new ProductGroupApi(getContext());
        call = productGroupApi.getAll(dateString);
        productGroupApi.runWebRequest(call, new WebCallBack<List<ProductGroupModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ProductGroupModel> result, Request request) {
                if (result.size() == 0) {
                    Timber.i("List of product groups was empty");
                    updateCall.success();
                } else {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.ProductGroup);
                        Timber.i("Updating product group completed");
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
                Timber.e(t.getMessage());
                updateCall.failure(getContext().getString(R.string.network_error));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.request_canceled));
            }
        });
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }
}
