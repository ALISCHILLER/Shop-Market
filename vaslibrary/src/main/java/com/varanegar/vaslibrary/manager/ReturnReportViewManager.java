package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.returnreportview.ReturnReportView;
import com.varanegar.vaslibrary.model.returnreportview.ReturnReportViewModel;
import com.varanegar.vaslibrary.model.returnreportview.ReturnReportViewModelRepository;


/**
 * Created by s.foroughi on 08/02/2017.
 */

public class ReturnReportViewManager extends BaseManager<ReturnReportViewModel> {
    public ReturnReportViewManager(Context context) {
        super(context , new ReturnReportViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(ReturnReportView.ReturnReportViewTbl);
        return query;
    }

    public static Query getAll(String key) {
        Query query = new Query();
        query.from(ReturnReportView.ReturnReportViewTbl).whereAnd(Criteria.contains(ReturnReportView.ProductCode, key));
        return query;
    }
}

