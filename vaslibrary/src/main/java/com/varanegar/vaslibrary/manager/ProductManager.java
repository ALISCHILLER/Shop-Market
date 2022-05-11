package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.mapper.ContentValueMap;
import com.varanegar.framework.database.mapper.ContentValueMapList;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.JoinFrom;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLinesManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.call.CallOrderLine;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.ReturnLines;
import com.varanegar.vaslibrary.model.call.ReturnLinesModel;
import com.varanegar.vaslibrary.model.catalog.Catalog;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.product.ProductModelRepository;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.product.PersonnelProductTemplateViewModel;
import com.varanegar.vaslibrary.webapi.product.ProductApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by atp on 4/12/2017.
 */

public class ProductManager extends BaseManager<ProductModel> {
    private Call<List<ProductModel>> call;
    private Call<List<PersonnelProductTemplateViewModel>> call2;

    public ProductManager(Context context) {
        super(context, new ProductModelRepository());
    }

    public List<ProductModel> getAll() {
        return getItems(new Query().from(Product.ProductTbl));
    }

    public static Query getProduct(UUID productId) {
        return new Query().from(Product.ProductTbl).whereAnd(Criteria.equals(Product.UniqueId, productId));
    }

    public void sync(final boolean forUpdate, @NonNull final UpdateCall updateCall) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.Product);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        final ProductApi productApi = new ProductApi(getContext());
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null) {
            updateCall.failure(getContext().getString(R.string.user_not_found));
            return;
        }
        final String dealerId = userModel.UniqueId.toString();
        call = productApi.getAll(dateString, dealerId , VaranegarApplication.getInstance().getAppId().toString());
        productApi.runWebRequest(call, new WebCallBack<List<ProductModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final List<ProductModel> result, Request request) {
                if (result.size() > 0) {
                    if (forUpdate) {
                        final CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                        final List<ProductModel> orderedProductModels = new ArrayList<>();
                        final ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
                        final List<ProductModel> returnProductsModels = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            if (result.get(i).isRemoved) {
                                List<CallOrderLineModel> callOrderLineModels = callOrderLineManager
                                        .getItems((new Query()).from(CallOrderLine.CallOrderLineTbl)
                                                .whereAnd(Criteria.equals(CallOrderLine.ProductUniqueId, result.get(i).UniqueId)));
                                if (callOrderLineModels.size() > 0)
                                    orderedProductModels.add(result.get(i));
                                List<ReturnLinesModel> returnLinesModels = returnLinesManager
                                        .getItems((new Query()).from(ReturnLines.ReturnLinesTbl)
                                                .whereAnd(Criteria.equals(ReturnLines.ProductUniqueId, result.get(i).UniqueId)));
                                if (returnLinesModels.size() > 0)
                                    returnProductsModels.add(result.get(i));
                            } else {
                                if (!(result.get(i).IsForSale)) {
                                    List<CallOrderLineModel> callOrderLineModels = callOrderLineManager
                                            .getItems((new Query()).from(CallOrderLine.CallOrderLineTbl)
                                                    .whereAnd(Criteria.equals(CallOrderLine.ProductUniqueId, result.get(i).UniqueId)));
                                    if (callOrderLineModels.size() > 0)
                                        orderedProductModels.add(result.get(i));
                                }
                                if (!(result.get(i).IsForReturnWithRef) && !(result.get(i).IsForReturnWithOutRef)) {
                                    List<ReturnLinesModel> returnLinesModels = returnLinesManager
                                            .getItems((new Query()).from(ReturnLines.ReturnLinesTbl)
                                                    .whereAnd(Criteria.equals(ReturnLines.ProductUniqueId, result.get(i).UniqueId)));
                                    if (returnLinesModels.size() > 0)
                                        returnProductsModels.add(result.get(i));
                                }
                            }
                        }
                        String orderProducts = "";
                        String returnProducts = "";
                        String fullMessage = "";
                        if (orderedProductModels.size() > 0)
                            for (ProductModel productModel : orderedProductModels)
                                orderProducts = orderProducts + productModel.ProductName + " " + productModel.ProductCode + " - ";
                        if (returnProductsModels.size() > 0)
                            for (ProductModel productModel : returnProductsModels)
                                returnProducts = returnProducts + productModel.ProductName + " " + productModel.ProductCode + " - ";
                        if (orderProducts.length() > 0)
                            fullMessage = getContext().getString(R.string.this_products_is_removed) + "\n" + orderProducts + "\n";
                        if (returnProducts.length() > 0)
                            fullMessage = fullMessage + getContext().getString(R.string.this_return_products_is_removed) + "\n" + returnProducts + "\n";
                        if (fullMessage.length() > 0) {
                            fullMessage = fullMessage + "\n" + getContext().getString(R.string.cannot_update_on_handqty);
                            updateCall.failure(fullMessage);
                        } else {
                            insertToProductTable(forUpdate, result, productApi, dealerId, updateCall);
                        }
                    } else {
                        insertToProductTable(forUpdate, result, productApi, dealerId, updateCall);
                    }
                } else {
                    Timber.i("Updating product, Product list was empty");
                    updatePersonnelProductData(updateCall, productApi, dealerId, forUpdate);
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

    private void insertToProductTable(boolean forUpdate, List<ProductModel> result,
                                      ProductApi productApi, String dealerId, UpdateCall updateCall) {
        try {
            sync(result);
            new UpdateManager(getContext()).addLog(UpdateKey.Product);
            Timber.i(result.size() + " rows of product updated");
            updatePersonnelProductData(updateCall, productApi, dealerId, forUpdate);
        } catch (ValidationException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.data_validation_failed));
        } catch (DbException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.data_error));
        }
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
        else if (call2 != null && !call2.isCanceled() && call2.isExecuted())
            call2.cancel();
    }

    private void updatePersonnelProductData(final UpdateCall updateCall,
                                            ProductApi productApi, String dealerId, final boolean forUpdate) {
        call2 = productApi.getPersonnelProductData(dealerId, VaranegarApplication.getInstance().getAppId().toString());
        productApi.runWebRequest(call2, new WebCallBack<List<PersonnelProductTemplateViewModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final List<PersonnelProductTemplateViewModel> result, Request request) {
                if (result != null && result.size() > 0) {
                    if (forUpdate) {
                        final CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                        final List<ProductModel> orderedProductModels = new ArrayList<>();
                        final ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
                        final List<ProductModel> returnProductsModels = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            if (!(result.get(i).IsForSale)) {
                                List<CallOrderLineModel> callOrderLineModels = callOrderLineManager
                                        .getItems((new Query()).from(CallOrderLine.CallOrderLineTbl)
                                                .whereAnd(Criteria.equals(CallOrderLine.ProductUniqueId, result.get(i).ProductId)));
                                if (callOrderLineModels.size() > 0) {
                                    ProductModel productModel = getItem(result.get(i).ProductId);
                                    orderedProductModels.add(productModel);
                                }
                            }
                            if (!(result.get(i).IsForReturnWithRef) && !(result.get(i).IsForReturnWithoutRef)) {
                                List<ReturnLinesModel> returnLinesModels = returnLinesManager
                                        .getItems((new Query()).from(ReturnLines.ReturnLinesTbl)
                                                .whereAnd(Criteria.equals(ReturnLines.ProductUniqueId, result.get(i).ProductId)));
                                if (returnLinesModels.size() > 0) {
                                    ProductModel productModel = getItem(result.get(i).ProductId);
                                    returnProductsModels.add(productModel);
                                }
                            }
                        }
                        String orderProducts = "";
                        String returnProducts = "";
                        String fullMessage = "";
                        if (orderedProductModels.size() > 0)
                            for (ProductModel productModel : orderedProductModels)
                                orderProducts = orderProducts + productModel.ProductName + " " + productModel.ProductCode + " - ";
                        if (returnProductsModels.size() > 0)
                            for (ProductModel productModel : returnProductsModels)
                                returnProducts = returnProducts + productModel.ProductName + " " + productModel.ProductCode + " - ";
                        if (orderProducts.length() > 0)
                            fullMessage = getContext().getString(R.string.this_products_is_removed) + "\n" + orderProducts + "\n";
                        if (returnProducts.length() > 0)
                            fullMessage = fullMessage + getContext().getString(R.string.this_return_products_is_removed) + "\n" + returnProducts + "\n";
                        if (fullMessage.length() > 0) {
                            fullMessage = fullMessage + "\n" + getContext().getString(R.string.cannot_update_on_handqty);
                            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                            cuteMessageDialog.setIcon(Icon.Warning);
                            cuteMessageDialog.setTitle(R.string.alert);
                            cuteMessageDialog.setMessage(fullMessage);
                            cuteMessageDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateCall.failure(getContext().getString(R.string.update_caceled));
                                    return;
                                }
                            });
                            cuteMessageDialog.show();
                        } else {
                            savePersonnelProductData(updateCall, result);
                        }
                    } else {
                        savePersonnelProductData(updateCall, result);
                    }
                } else {
                    Timber.i("Personnel Product Template list was empty");
                    updateCall.success();
                }
            }

            private void savePersonnelProductData(final UpdateCall updateCall, final List<PersonnelProductTemplateViewModel> result) {
                try {
                    long affectedRows = update(new ContentValueMapList<PersonnelProductTemplateViewModel, ProductModel>(result) {
                        @Override
                        protected ContentValueMap<PersonnelProductTemplateViewModel, ProductModel>
                        getContentValueMap(PersonnelProductTemplateViewModel item) {
                            return new ContentValueMap<PersonnelProductTemplateViewModel, ProductModel>(ProductModel.class)
                                    .map(item.IsForSale, Product.IsForSale)
                                    .map(item.IsForCount, Product.IsForCount)
                                    .map(item.IsForRequest, Product.IsForRequest)
                                    .map(item.IsForReturnWithoutRef, Product.IsForReturnWithOutRef)
                                    .map(item.IsForReturnWithRef, Product.IsForReturnWithRef);
                        }

                        @Override
                        public UUID getUniqueId(PersonnelProductTemplateViewModel item) {
                            return item.ProductId;
                        }
                    });
                    Timber.i("Personnel Product Template updated " + affectedRows + " rows.");
                    updateCall.success();
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

    public List<ProductModel> getForSaleItemsOrderByCatalog(UUID catalogId) {
        Query query = new Query().from(JoinFrom.table(Product.ProductTbl).innerJoin(Catalog.CatalogTbl)
                .on(Product.UniqueId, Catalog.ProductId)
                .onAnd(Criteria.equals(Catalog.CatalogId, catalogId.toString()))
                .onAnd(Criteria.equals(Product.IsForSale, true)))
                .orderByAscending(Catalog.OrderOf);
        return getItems(query);
    }

    public List<ProductModel> getItemsOfSubGroups(@NonNull UUID[] groupIds, boolean isForSale) {
        Query query = new Query().from(Product.ProductTbl);
        String[] ids = new String[groupIds.length];
        for (int i = 0; i < groupIds.length; i++) {
            ids[i] = groupIds[i].toString();
        }
        query.whereAnd(Criteria.in(Product.ProductGroupId, ids));
        query.whereAnd(Criteria.equals(Product.IsForSale, isForSale));
        return getItems(query);
    }

    @Nullable
    public Integer getBackOfficeId(UUID uniqueId) {
        Query query = new Query();
        query.from(Product.ProductTbl)
                .whereAnd(Criteria.equals(Product.UniqueId, uniqueId))
                .select(Product.BackOfficeId);
        return VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
    }

    @Nullable
    public String getIdByBackofficeId(int backofficeId) {
        Query query = new Query();
        query.from(Product.ProductTbl)
                .whereAnd(Criteria.equals(Product.BackOfficeId, backofficeId))
                .select(Product.UniqueId);
        return VaranegarApplication.getInstance().getDbHandler().getStringSingle(query);
    }

    public List<ProductModel> getAll(ProductType productType) {
        if (productType == ProductType.isForSale)
            return getItems(new Query().from(Product.ProductTbl)
                    .whereAnd(Criteria.equals(Product.IsForSale, true)));
        else if (productType == ProductType.isForReturn)
            return getItems(new Query().from(Product.ProductTbl)
                    .whereAnd(Criteria.equals(Product.IsForReturnWithOutRef, true)
                    .or(Criteria.equals(Product.IsForReturnWithRef, true))));
        else
            return getItems(new Query().from(Product.ProductTbl));
    }

    public ProductModel getProductByBackOfficeId (int backOfficeId) {
        return getItem(new Query().from(Product.ProductTbl)
                .whereAnd(Criteria.equals(Product.BackOfficeId, backOfficeId)));
    }
}
