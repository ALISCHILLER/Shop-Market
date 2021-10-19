package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.productreportview.ProductReportView;
import com.varanegar.vaslibrary.model.productreportview.ProductReportViewModel;
import com.varanegar.vaslibrary.model.productreportview.ProductReportViewModelRepository;

/**
 * Created by A.Jafarzadeh on 12/8/2018.
 */

public class ProductReportViewManager extends BaseManager<ProductReportViewModel> {
    public ProductReportViewManager(@NonNull Context context) {
        super(context, new ProductReportViewModelRepository());
    }

    public static Query getAll() {
        return new Query().from(ProductReportView.ProductReportViewTbl);
    }

}
