package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogView;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogViewModel;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogViewModelRepository;

/**
 * Created by A.Torabi on 8/6/2017.
 */

public class ProductGroupCatalogViewManager extends BaseManager<ProductGroupCatalogViewModel> {
    public ProductGroupCatalogViewManager(@NonNull Context context) {
        super(context, new ProductGroupCatalogViewModelRepository());
    }

    public static Query getAll() {
        return new Query().from(ProductGroupCatalogView.ProductGroupCatalogViewTbl).orderByAscending(ProductGroupCatalogView.RowIndex);
    }


}
