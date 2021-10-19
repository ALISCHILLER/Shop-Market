package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.SalesmanReportView.SalesmanReportView;
import com.varanegar.vaslibrary.model.SalesmanReportView.SalesmanReportViewModel;
import com.varanegar.vaslibrary.model.SalesmanReportView.SalesmanReportViewModelRepository;

/**
 * Created by s.foroughi on 11/02/2017.
 */

public class SalesmanReportViewManager extends BaseManager<SalesmanReportViewModel> {
    public SalesmanReportViewManager(Context context){

        super(context , new SalesmanReportViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(SalesmanReportView.SalesmanReportViewTbl);
        return query;
    }

    public static Query getAll(String key) {
        Query query = new Query();
        query.from(SalesmanReportView.SalesmanReportViewTbl).whereAnd(Criteria.contains(SalesmanReportView.CustomerCode, key));
        return query;
    }
}
