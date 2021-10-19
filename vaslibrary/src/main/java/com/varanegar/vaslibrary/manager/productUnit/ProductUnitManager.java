package com.varanegar.vaslibrary.manager.productUnit;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.framework.validation.Validator;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.UnitManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLineQtyManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyModel;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnit.ProductUnit;
import com.varanegar.vaslibrary.model.productUnit.ProductUnitModel;
import com.varanegar.vaslibrary.model.productUnit.ProductUnitModelRepository;
import com.varanegar.vaslibrary.model.unit.UnitModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.product.ProductUnitApi;

import com.varanegar.framework.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/26/2017.
 */

public class ProductUnitManager extends BaseManager<ProductUnitModel>
{
    public ProductUnitManager(@NonNull Context context)
    {
        super(context, new ProductUnitModelRepository());
    }

    public void sync(final boolean forUpdate, @NonNull final UpdateCall updateCall)
    {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.ProductUnit);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        ProductUnitApi productUnitApi = new ProductUnitApi(getContext());
        productUnitApi.runWebRequest(productUnitApi.getAll(dateString), new WebCallBack<List<ProductUnitModel>>()
        {
            @Override
            protected void onFinish()
            {

            }

            @Override
            protected void onSuccess(List<ProductUnitModel> result, Request request)
            {
                if (result.size()> 0) {
                    if (forUpdate) {
                        OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getContext());
                        List<ProductUnitModel> orderLineQty = new ArrayList<>();
                        ReturnLineQtyManager returnLineQtyManager = new ReturnLineQtyManager(getContext());
                        List<ProductUnitModel> returnLineQty = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            if (result.get(i).isRemoved) {
                                List<OrderLineQtyModel> orderLineQtyModel = orderLineQtyManager.getItems(orderLineQtyManager.getProductUnitLines(result.get(i).UniqueId));
                                if (orderLineQtyModel.size() > 0)
                                    orderLineQty.add(result.get(i));
                                List<ReturnLineQtyModel> returnLinesModels = returnLineQtyManager.getItems(returnLineQtyManager.getProductUnitLines(result.get(i).UniqueId));
                                if (returnLinesModels.size() > 0)
                                    returnLineQty.add(result.get(i));
                            } else {
                                if (!(result.get(i).IsForSale)) {
                                    List<OrderLineQtyModel> orderLineQtyModel = orderLineQtyManager.getItems(orderLineQtyManager.getProductUnitLines(result.get(i).UniqueId));
                                    if (orderLineQtyModel.size() > 0)
                                        orderLineQty.add(result.get(i));
                                }
                                if (!(result.get(i).IsForReturn)) {
                                    List<ReturnLineQtyModel> returnLinesModels = returnLineQtyManager.getItems(returnLineQtyManager.getProductUnitLines(result.get(i).UniqueId));
                                    if (returnLinesModels.size() > 0)
                                        returnLineQty.add(result.get(i));
                                }
                            }
                        }
                        String orderProducts = "";
                        String returnProducts = "";
                        String fullMessage = "";
                        if (orderLineQty.size() > 0)
                            for (ProductUnitModel productUnitModel : orderLineQty) {
                                ProductManager productManager = new ProductManager(getContext());
                                ProductModel productModel = productManager.getItem(productUnitModel.ProductId);
                                UnitManager unitManager = new UnitManager(getContext());
                                UnitModel unitModel = unitManager.getItem(productUnitModel.UnitId);
                                orderProducts = orderProducts + "- " + productModel.ProductName + " (" + productModel.ProductCode + ") " + unitModel.UnitName + "\n";
                            }
                        if (returnLineQty.size() > 0)
                            for (ProductUnitModel productUnitModel : returnLineQty) {
                                ProductManager productManager = new ProductManager(getContext());
                                ProductModel productModel = productManager.getItem(productUnitModel.ProductId);
                                UnitManager unitManager = new UnitManager(getContext());
                                UnitModel unitModel = unitManager.getItem(productUnitModel.UnitId);
                                returnProducts = returnProducts + "- " + productModel.ProductName + " (" + productModel.ProductCode + ") " + unitModel.UnitName + "\n";
                            }
                        if (orderProducts.length() > 0)
                            fullMessage = getContext().getString(R.string.this_products_unit_is_removed) + "\n" + orderProducts;
                        if (returnProducts.length() > 0)
                            fullMessage = fullMessage + getContext().getString(R.string.this_return_product_unit_is_removed) + "\n" + returnProducts;
                        if (fullMessage.length() > 0) {
                            fullMessage = fullMessage + getContext().getString(R.string.cannot_update_on_handqty);
                            updateCall.failure(fullMessage);
                        } else {
                            try {
                                sync(result);
                                new UpdateManager(getContext()).addLog(UpdateKey.ProductUnit);
                                Timber.i("Updating productunit completed");
                                updateCall.success();
                            } catch (ValidationException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_validation_failed));
                            } catch (DbException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_error));
                            }
                        }
                    } else {
                        try {
                            sync(result);
                            new UpdateManager(getContext()).addLog(UpdateKey.ProductUnit);
                            Timber.i("Updating productunit completed");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    }
                } else {
                    Timber.i("Updating product unit completed. list was empty");
                    updateCall.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request)
            {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request)
            {
                Timber.e(t.getMessage());
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public ProductUnitModel getItem(String productId, String unitId) {
        Query query = new Query();
        query.from(ProductUnit.ProductUnitTbl).whereAnd(Criteria.equals(ProductUnit.ProductId, productId).and(Criteria.equals(ProductUnit.UnitId, unitId)));
        return getItem(query);
    }

}
