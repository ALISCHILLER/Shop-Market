package com.varanegar.vaslibrary.manager.catalogmanager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.from.JoinFrom;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ProductGroupCatalogManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.catalog.Catalog;
import com.varanegar.vaslibrary.model.catalog.CatalogModel;
import com.varanegar.vaslibrary.model.catalog.CatalogModelRepository;
import com.varanegar.vaslibrary.model.image.ImageModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQty;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalog;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.catalog.CatalogApi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 6/21/2017.
 */

public class CatalogManager extends BaseManager<CatalogModel> {

    public static final UUID BASED_ON_PRODUCT_GROUP = UUID.fromString("da6b7654-abe3-40da-b60f-f7d9134e964e");
    public static final UUID BASED_ON_PRODUCT = UUID.fromString("1465CE98-8DB1-46C3-BAFA-81A98AB0B502");

    public CatalogManager(@NonNull Context context) {
        super(context, new CatalogModelRepository());
    }

    public static Query getAll(boolean inStock, @Nullable String search) {
        if (search != null && !search.isEmpty()){
            search = VasHelperMethods.persian2Arabic(search);
            search = VasHelperMethods.convertToEnglishNumbers(search);
        }
        if (inStock) {
            Query query = new Query();
            JoinFrom from = From.table(Catalog.CatalogTbl).leftJoin(Product.ProductTbl)
                    .on(Catalog.ProductId, Product.UniqueId)
                    .leftJoin(OnHandQty.OnHandQtyTbl)
                    .on(OnHandQty.ProductId, Catalog.ProductId)
                    .innerJoin(ProductGroupCatalog.ProductGroupCatalogTbl)
                    .on(ProductGroupCatalog.UniqueId, Catalog.CatalogId);

            query = query.from(from)
                    .whereAnd(Criteria.equals(Product.IsForSale, true))
                    .whereAnd(Criteria.equals(Product.IsActive, true))
                    .whereAnd(Criteria.greaterThan(OnHandQty.OnHandQty, 0));
            if (search != null && !search.isEmpty())
                query.whereAnd(Criteria.contains(Product.ProductName, search).or(Criteria.contains(Product.ProductCode, search)));
            return query.groupBy(Catalog.CatalogId).orderByAscending(ProductGroupCatalog.RowIndex);
        } else {
            Query query = new Query();
            JoinFrom from = From.table(Catalog.CatalogTbl).leftJoin(Product.ProductTbl)
                    .on(Catalog.ProductId, Product.UniqueId)
                    .innerJoin(ProductGroupCatalog.ProductGroupCatalogTbl)
                    .on(ProductGroupCatalog.UniqueId, Catalog.CatalogId);

            query = query.from(from)
                    .whereAnd(Criteria.equals(Product.IsForSale, true))
                    .whereAnd(Criteria.equals(Product.IsActive, true));

            if (search != null && !search.isEmpty())
                query.whereAnd(Criteria.contains(Product.ProductName, search).or(Criteria.contains(Product.ProductCode, search)));

            return query.groupBy(Catalog.CatalogId).orderByAscending(ProductGroupCatalog.RowIndex);
        }
    }

    public static Query getAll(UUID catalogId, boolean inStock, @Nullable String search) {
        if (search != null && !search.isEmpty()){
            search = VasHelperMethods.persian2Arabic(search);
            search = VasHelperMethods.convertToEnglishNumbers(search);
        }
        if (inStock) {
            Query query = new Query();
            query.from(From.table(Catalog.CatalogTbl).innerJoin(Product.ProductTbl)
                    .on(Catalog.ProductId, Product.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForSale, true))
                    .onAnd(Criteria.equals(Product.IsActive, true))
                    .innerJoin(OnHandQty.OnHandQtyTbl)
                    .on(OnHandQty.ProductId, Catalog.ProductId)
                    .onAnd(Criteria.greaterThan(OnHandQty.OnHandQty, 0)))
                    .whereAnd(Criteria.equals(Catalog.CatalogId, catalogId.toString()));

            if (search != null && !search.isEmpty())
                query.whereAnd(Criteria.contains(Product.ProductName, search).or(Criteria.contains(Product.ProductCode, search)));
            query.orderByAscending(Catalog.OrderOf);
            return query;
        } else {
            Query query = new Query();
            query.from(From.table(Catalog.CatalogTbl).innerJoin(Product.ProductTbl)
                    .on(Catalog.ProductId, Product.UniqueId)
                    .onAnd(Criteria.equals(Product.IsForSale, true))
                    .onAnd(Criteria.equals(Product.IsActive, true)))
                    .whereAnd(Criteria.equals(Catalog.CatalogId, catalogId.toString()));

            if (search != null && !search.isEmpty())
                query.whereAnd(Criteria.contains(Product.ProductName, search).or(Criteria.contains(Product.ProductCode, search)));
            query.orderByAscending(Catalog.OrderOf);
            return query;
        }
    }

    public boolean hasFile() {
        File file = HelperMethods.getExternalFilesDir(getContext(), ImageManager.ImagesFolder);
        if (file == null)
            return false;
        File[] files = file.listFiles();
        return files.length > 0;
    }

    public void sync(final UpdateCall call) {
        CatalogApi api = new CatalogApi(getContext());
        api.runWebRequest(api.getCatalogs(DateHelper.toString(UpdateManager.MIN_DATE, DateFormat.MicrosoftDateTime, Locale.US)), new WebCallBack<List<CatalogModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CatalogModel> result, Request request) {
                try {
                    deleteAll();
                    if (result.size() == 0) {
                        Timber.i("catalog is empty");
                        call.success();
                    } else {
                        try {
                            insert(result);
                            Timber.i("Updating catalogs finished");
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_error));
                        }
                    }
                } catch (DbException e) {
                    Timber.e(e);
                    call.failure(getContext().getString(R.string.error_deleting_old_data));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t, "downloading catalog info Failed");
                call.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public void syncPhotos(final boolean groupCatalog, final boolean forceDownloadIfExists, final ImageManager.ImageDownloadCallBack callBack) {
        final ImageManager imageManager = new ImageManager(getContext());
        imageManager.sync(new UpdateCall() {
            @Override
            protected void onFinish() {
                if (groupCatalog) {
                    ProductGroupCatalogManager productGroupCatalogManager = new ProductGroupCatalogManager(getContext());
                    List<ProductGroupCatalogModel> catalogModels = productGroupCatalogManager.getAll();
                    Set<UUID> tokenIds = new HashSet<>();
                    for (ProductGroupCatalogModel productGroupCatalogModel :
                            catalogModels) {
                        tokenIds.add(productGroupCatalogModel.UniqueId);
                    }
                    List<ImageModel> images = imageManager.getItems(ImageType.CatalogLarge, ImageType.CatalogSmall);
                    List<ImageModel> imagesToDownload = new ArrayList<>();
                    for (ImageModel image :
                            images) {
                        if (tokenIds.contains(image.TokenId))
                            imagesToDownload.add(image);
                    }
                    imageManager.downloadImage(imagesToDownload, forceDownloadIfExists, callBack);
                } else {
                    List<ImageModel> images = imageManager.getItems(ImageType.Product, ImageType.CatalogSmall);
                    imageManager.downloadImage(images, forceDownloadIfExists, callBack);
                }
            }

            @Override
            protected void onSuccess() {
                super.onSuccess();
            }

            @Override
            protected void onFailure(String error) {
                super.onFailure(error);
            }
        });
    }

    public void prepareCatalogue(boolean groupCatalog) {
        ImageManager imageManager = new ImageManager(getContext());
        List<ImageModel> images;
        if (groupCatalog)
            images = imageManager.getItems(ImageType.CatalogLarge, ImageType.CatalogSmall);
        else
            images = imageManager.getItems(ImageType.Product, ImageType.CatalogSmall);
        for (ImageModel image :
                images) {
            String path = imageManager.getImagePath(image);
            int fileSize = ImageManager.getFileSize(path);
            int dstWidth = 1000;
            if (image.getImageType() == ImageType.CatalogSmall)
                dstWidth = 200;
            try {
                if (fileSize > 1000)
                    ImageManager.compress(path, 30, dstWidth);
                else if (fileSize > 500)
                    ImageManager.compress(path, 50, dstWidth);
                else if (fileSize > 200)
                    ImageManager.compress(path, 60, dstWidth);
                else if (fileSize > 100 && image.getImageType() == ImageType.CatalogSmall)
                    ImageManager.compress(path, 60, dstWidth);
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }

    }

}
