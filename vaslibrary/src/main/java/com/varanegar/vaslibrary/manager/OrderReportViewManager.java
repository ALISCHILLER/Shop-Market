package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.orderreportview.OrderReportView;
import com.varanegar.vaslibrary.model.orderreportview.OrderReportViewModel;
import com.varanegar.vaslibrary.model.orderreportview.OrderReportViewModelRepository;

/**
 * Created by A.Jafarzadeh on 8/10/2017.
 */

public class OrderReportViewManager extends BaseManager<OrderReportViewModel> {
    public OrderReportViewManager(@NonNull Context context) {
        super(context, new OrderReportViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(OrderReportView.OrderReportViewTbl);
        return query;
    }
}
