package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportView;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModelRepository;

import java.util.UUID;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public class RequestReportViewManager extends BaseManager<RequestReportViewModel> {
    public RequestReportViewManager(Context context) {
        super(context , new RequestReportViewModelRepository());
    }

    public static Query getCurrentCustomer(UUID customerId)
    {
        Query query = new Query();
        query.from(RequestReportView.RequestReportViewTbl).whereAnd(Criteria.equals(RequestReportView.UniqueId, customerId));
        return query;
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(RequestReportView.RequestReportViewTbl);
        return query;
    }

    public static Query getAll(String key) {
        Query query = new Query();
        query.from(RequestReportView.RequestReportViewTbl).whereAnd(Criteria.contains(RequestReportView.CustomerCode, key));
        return query;
    }


}

