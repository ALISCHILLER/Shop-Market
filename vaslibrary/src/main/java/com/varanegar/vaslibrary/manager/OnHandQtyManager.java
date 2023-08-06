package com.varanegar.vaslibrary.manager;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetail;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQty;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModelRepository;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productobatchnhandqtyviewmodek.ProductBatchOnHandViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.onhandqty.BatchOnHandQtyApi;
import com.varanegar.vaslibrary.webapi.onhandqty.DistOnHandQtyApi;
import com.varanegar.vaslibrary.webapi.onhandqty.HotSalesOnHandQtyApi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 7/10/2017.
 */

public class OnHandQtyManager extends BaseManager<OnHandQtyModel> {

    public static class OrderPointCheckType {
        public static UUID ShowQty = UUID.fromString("8753F7BB-49F8-47CE-BD86-DEB77069AC7A");
        public static UUID ShowAvailability = UUID.fromString("9435E107-9873-4284-8D4E-52A9968C61F1");
    }

    public OnHandQtyManager(@NonNull Context context) {
        super(context, new OnHandQtyModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall, final boolean renew, String customerCode) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            String tourId = new TourManager(getContext()).loadTour().UniqueId.toString();
            DistOnHandQtyApi distOnHandQtyApi = new DistOnHandQtyApi(getContext());
            distOnHandQtyApi.runWebRequest(distOnHandQtyApi.getAll(tourId), new WebCallBack<List<OnHandQtyModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<OnHandQtyModel> result, Request request) {
                    try {
                        deleteAll();
                        if (result != null && result.size() > 0)
                            try {
                                insert(result);
                                new UpdateManager(getContext()).addLog(UpdateKey.OnHandQty);
                                Timber.i("Updating onhandqty completed");
                                updateCall.success();
                            } catch (ValidationException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_validation_failed));
                            } catch (DbException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_error));
                            }
                        else {
                            Timber.i("Updating onhandqty completed. List was empty");
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
                    Timber.e(t.getMessage());
                    updateCall.failure(getContext().getString(R.string.network_error));
                }
            });
        }
        else if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            try {
                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
                BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
//                if (backOfficeType == BackOfficeType.Varanegar) {
                BatchOnHandQtyApi batchOnHandQtyApi = new BatchOnHandQtyApi(getContext());
                batchOnHandQtyApi.runWebRequest(batchOnHandQtyApi.getAll(
                        UserManager.readFromFile(getContext())
                                .UniqueId.toString(), sysConfigModel.Value,customerCode), new WebCallBack<ProductBatchOnHandViewModel>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(ProductBatchOnHandViewModel result, Request request) {
                        try {
                            deleteAll();
                            if (result != null) {
                                if (result.OnHandQty != null && result.OnHandQty.size() > 0) {
                                    try {
                                        insert(result.OnHandQty);
                                        new UpdateManager(getContext()).addLog(UpdateKey.BatchOnHandQty);
                                        Timber.i("Updating onhandqty completed");
                                        ProductBatchOnHandQtyManager productBatchOnHandQtyManager = new ProductBatchOnHandQtyManager(getContext());
                                        productBatchOnHandQtyManager.sync(result.BatchOnHandQty, updateCall);
                                    } catch (ValidationException e) {
                                        Timber.e(e);
                                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                                    } catch (DbException e) {
                                        Timber.e(e);
                                        updateCall.failure(getContext().getString(R.string.data_error));
                                    }
                                } else {
                                    Timber.i("Updating onhandqty completed. List was empty");
                                    updateCall.success();
                                }
                            } else {
                                Timber.i("Updating onhandqty completed. List was empty");
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
                        Timber.e(t.getMessage());
                        updateCall.failure(getContext().getString(R.string.network_error));
                    }
                });
//                } else {
//                    UpdateManager updateManager = new UpdateManager(getContext());
//                    Date date = updateManager.getLog(UpdateKey.OnHandQty);
//                    String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
//                    OnHandQtyAPI onHandQtyAPI = new OnHandQtyAPI(getContext());
//                    onHandQtyAPI.runWebRequest(onHandQtyAPI.getAll(dateString, UserManager.readFromFile(getContext()).UniqueId.toString()), new WebCallBack<List<OnHandQtyModel>>() {
//                        @Override
//                        protected void onFinish() {
//
//                        }
//
//                        @Override
//                        protected void onSuccess(List<OnHandQtyModel> result, Request request) {
//                            try {
//                                deleteAll();
//                                if (result != null && result.size() > 0)
//                                    try {
//                                        insert(result);
//                                        new UpdateManager(getContext()).addLog(UpdateKey.OnHandQty);
//                                        Timber.i("Updating onhandqty completed");
//                                        updateCall.success();
//                                    } catch (ValidationException e) {
//                                        Timber.e(e);
//                                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
//                                    } catch (DbException e) {
//                                        Timber.e(e);
//                                        updateCall.failure(getContext().getString(R.string.data_error));
//                                    }
//                                else {
//                                    Timber.i("Updating onhandqty completed. List was empty");
//                                    updateCall.success();
//                                }
//                            } catch (DbException e) {
//                                Timber.e(e);
//                                updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
//                            }
//                        }
//
//                        @Override
//                        protected void onApiFailure(ApiError error, Request request) {
//                            String err = WebApiErrorBody.log(error, getContext());
//                            updateCall.failure(err);
//                        }
//
//                        @Override
//                        protected void onNetworkFailure(Throwable t, Request request) {
//                            Timber.e(t.getMessage());
//                            updateCall.failure(getContext().getString(R.string.network_error));
//                        }
//                    });
//                }
            } catch (Exception e) {
                updateCall.failure(getContext().getString(R.string.back_office_type_is_uknown));
            }
        } else {
            final List<OnHandQtyModel> oldOnHandQtyModels = getItems(new Query().from(OnHandQty.OnHandQtyTbl));

            HotSalesOnHandQtyApi hotSalesOnHandQtyApi = new HotSalesOnHandQtyApi(getContext());
            TourManager tourManager = new TourManager(getContext());
            TourModel tourModel = tourManager.loadTour();
            Call<List<OnHandQtyModel>> call;
            if (renew)
                call = hotSalesOnHandQtyApi.renew(UserManager.readFromFile(getContext()).UniqueId.toString(), tourModel.UniqueId.toString());
            else
                call = hotSalesOnHandQtyApi.getAll(UserManager.readFromFile(getContext()).UniqueId.toString(), tourModel.UniqueId.toString());

            hotSalesOnHandQtyApi.runWebRequest(call, new WebCallBack<List<OnHandQtyModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(final List<OnHandQtyModel> result, Request request) {
                    try {
                        if (renew && VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
                            List<WarehouseProductQtyViewModel> conflictProducts = new ArrayList<>();
                            List<WarehouseProductQtyViewModel> warehouseProductQtyViewModels = new
                                    WarehouseProductQtyViewManager(getContext())
                                    .getItems(WarehouseProductQtyViewManager.getAll());
                            HashMap<UUID, BigDecimal> newOnHandQty = new HashMap<>();
                            for (OnHandQtyModel newOnHandQtyModel :
                                    result) {
                                newOnHandQty.put(newOnHandQtyModel.ProductId, newOnHandQtyModel.OnHandQty);
                            }
                            for (WarehouseProductQtyViewModel warehouseProductQtyViewModel :
                                    warehouseProductQtyViewModels) {
                                BigDecimal orderValue = warehouseProductQtyViewModel.TotalQty;
                                BigDecimal newValue = newOnHandQty.get(warehouseProductQtyViewModel.UniqueId);
                                if ((newValue == null && orderValue.compareTo(BigDecimal.ZERO) > 0) || (newValue != null && orderValue.compareTo(newValue) > 0))
                                    conflictProducts.add(warehouseProductQtyViewModel);
                            }
                            if (conflictProducts.size() > 0) {
                                String error = getContext().getString(R.string.stock_qty_conflict) + "\n";
                                for (WarehouseProductQtyViewModel conflictProduct :
                                        conflictProducts) {
                                    error += getContext().getString(R.string.product_code_label) + conflictProduct.ProductCode + "\n";
                                }
                                updateCall.failure(error);
                                return;
                            }
                        }
                        deleteAll();
                        if (result != null && result.size() > 0) {
                            final ProductManager productManager = new ProductManager(getContext());
                            HashMap<UUID, OnHandQtyModel> onHadQtyMap = new HashMap<>();
                            for (OnHandQtyModel onHandQtyModel :
                                    oldOnHandQtyModels) {
                                onHadQtyMap.put(onHandQtyModel.UniqueId, onHandQtyModel);
                            }

                            final List<ProductModel> products = productManager.getItems(new Query().from(Product.ProductTbl));
                            HashMap<UUID, ProductModel> productsMap = new HashMap<>();
                            for (ProductModel productModel :
                                    products) {
                                productsMap.put(productModel.UniqueId, productModel);
                                productModel.IsForSale = false;
                            }

                            for (OnHandQtyModel newOnHandQty :
                                    result) {
                                if (onHadQtyMap.containsKey(newOnHandQty.UniqueId)) {
                                    BigDecimal oldValue = onHadQtyMap.get(newOnHandQty.UniqueId).OnHandQty;
                                    if (oldValue == null)
                                        oldValue = BigDecimal.ZERO;
                                    BigDecimal newValue = newOnHandQty.OnHandQty;
                                    if (newValue == null)
                                        newValue = BigDecimal.ZERO;
                                    newOnHandQty.RenewQty = newValue.subtract(oldValue);
                                }
//                                if (productsMap.containsKey(newOnHandQty.ProductId)) {
//                                    ProductModel product = productsMap.get(newOnHandQty.ProductId);
//                                    product.IsForSale = true;
//                                }
                            }

                            try {
                                insert(result);
                                Timber.i("Updating onhandqty completed");
//                                productManager.update(products);
                                new UpdateManager(getContext()).addLog(UpdateKey.HotSalesOnHandQty);
                                updateCall.success();
                            } catch (ValidationException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_validation_failed));
                            } catch (DbException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_error));
                            }
                        } else {
                            Timber.i("Updating onhandqty completed. List was empty");
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
                    Timber.e(t.getMessage());
                    updateCall.failure(getContext().getString(R.string.network_error));
                }
            });

        }
    }

    @Nullable
    public static Pair<String, Integer> showInventoryQty(@NonNull Context context, @NonNull OnHandQtyStock onHandQtyStock, @Nullable SysConfigModel showStockLevel, @Nullable SysConfigModel orderPointCheckType) {
        if (onHandQtyStock.ProductTotalOrderedQty == null)
            onHandQtyStock.ProductTotalOrderedQty = BigDecimal.ZERO;
        if (onHandQtyStock.OnHandQty == null)
            onHandQtyStock.OnHandQty = BigDecimal.ZERO;
        if (onHandQtyStock.OrderPoint == null)
            onHandQtyStock.OrderPoint = BigDecimal.ZERO;
        if (onHandQtyStock.TotalQty == null)
            onHandQtyStock.TotalQty = BigDecimal.ZERO;
        BigDecimal remained = onHandQtyStock.OnHandQty.subtract(onHandQtyStock.ProductTotalOrderedQty).add(onHandQtyStock.TotalQty);
        BigDecimal qty;
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && !onHandQtyStock.HasAllocation)
            qty = onHandQtyStock.OnHandQty;
        else
            qty = remained;

        if (SysConfigManager.compare(showStockLevel, true)) {
            if (SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty)) {
                if (qty.compareTo(onHandQtyStock.OrderPoint) <= 0) {
                    return new Pair<>(context.getString(R.string.multiplication_sign), R.color.red);
                } else {
                    String txt = VasHelperMethods.chopTotalQtyToString(qty, onHandQtyStock.UnitNames, onHandQtyStock.ConvertFactors, ":", ":");
                    if (remained.compareTo(onHandQtyStock.OrderPoint) >= 0)
                        return new Pair<>(txt, R.color.green);
                    else
                        return new Pair<>(txt, R.color.orange);
                }
            } else {
                if (qty.compareTo(onHandQtyStock.OrderPoint) <= 0) {
                    return new Pair<>(context.getString(R.string.multiplication_sign), R.color.red);
                } else {
                    if (remained.compareTo(onHandQtyStock.OrderPoint) >= 0)
                        return new Pair<>(context.getString(R.string.check_sign), R.color.green);
                    else
                        return new Pair<>(context.getString(R.string.multiplication_sign), R.color.orange);
                }
            }
        } else {
            return null;
        }
    }



    public List<OnHandQtyModel> getAll() {
        return getItems(new Query().from(OnHandQty.OnHandQtyTbl));
    }

    @Nullable
    public OnHandQtyModel getOnHandQty(UUID productId) {
        return getItem(new Query().from(OnHandQty.OnHandQtyTbl).whereAnd(Criteria.equals(OnHandQty.ProductId, productId)));
    }

}
