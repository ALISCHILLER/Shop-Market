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
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.catalog.Catalog;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalog;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogModel;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.catalog.ProductGroupCatalogApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 8/6/2017.
 */

public class ProductGroupCatalogManager extends BaseManager<ProductGroupCatalogModel> {
    public ProductGroupCatalogManager(@NonNull Context context) {
        super(context, new ProductGroupCatalogModelRepository());
    }


    public void sync(final UpdateCall updateCall) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.ProductCatalogGroup);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        ProductGroupCatalogApi productCatalogGroupApi = new ProductGroupCatalogApi(getContext());
        productCatalogGroupApi.runWebRequest(productCatalogGroupApi.getCatalogs(), new WebCallBack<List<ProductGroupCatalogModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ProductGroupCatalogModel> result, Request request) {
                try {
                    deleteAll();
                    if (result.size() == 0) {
                        Timber.i("product group catalog was empty ");
                        updateCall.success();
                    } else {
                        try {
                            sync(result);
                            new UpdateManager(getContext()).addLog(UpdateKey.ProductCatalogGroup);
                            Timber.i("Updating product group catalogs completed");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
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

    public static Query getAll(UUID groupId) {
        /*
        SELECT ProductGroupCatalog.* FROM ProductGroupCatalog
        INNER JOIN Catalog ON Catalog.CatalogId = ProductGroupCatalog.UniqueId
        INNER JOIN Product ON Catalog.ProductId = Product.UniqueId AND Product.IsForSale = 1
        WHERE ProductGroupCatalog.ProductMainGroupId = '486b80d9-fc50-460e-bff2-7e8ecf30fbe6'
        GROUP BY Catalog.CatalogId
        */

        return new Query().select(ProductGroupCatalog.UniqueId, ProductGroupCatalog.ProductMainGroupId, ProductGroupCatalog.CatalogName, ProductGroupCatalog.RowIndex
        ).from(JoinFrom.table(ProductGroupCatalog.ProductGroupCatalogTbl).innerJoin(Catalog.CatalogTbl)
                .on(Catalog.CatalogId, ProductGroupCatalog.UniqueId)
                .innerJoin(Product.ProductTbl).on(Catalog.ProductId, Product.UniqueId)
                .onAnd(Criteria.equals(Product.IsForSale, true))
                .onAnd(Criteria.equals(Product.IsActive, true)))
                .whereAnd(Criteria.equals(ProductGroupCatalog.ProductMainGroupId, groupId.toString())
                ).groupBy(Catalog.CatalogId).orderByAscending(ProductGroupCatalog.RowIndex);
    }

    public List<ProductGroupCatalogModel> getAll() {
        /*
        SELECT ProductGroupCatalog.* FROM ProductGroupCatalog
        INNER JOIN Catalog ON Catalog.CatalogId = ProductGroupCatalog.UniqueId
        INNER JOIN Product ON Catalog.ProductId = Product.UniqueId AND Product.IsForSale = 1
        GROUP BY Catalog.CatalogId
        */

        Query q = new Query().select(ProductGroupCatalog.UniqueId, ProductGroupCatalog.ProductMainGroupId, ProductGroupCatalog.CatalogName, ProductGroupCatalog.RowIndex
        ).from(JoinFrom.table(ProductGroupCatalog.ProductGroupCatalogTbl).innerJoin(Catalog.CatalogTbl)
                .on(Catalog.CatalogId, ProductGroupCatalog.UniqueId)
                .innerJoin(Product.ProductTbl).on(Catalog.ProductId, Product.UniqueId)
                .onAnd(Criteria.equals(Product.IsForSale, true))
                .onAnd(Criteria.equals(Product.IsActive, true)))
                .groupBy(Catalog.CatalogId).orderByAscending(ProductGroupCatalog.RowIndex);
        return getItems(q);
    }


}
