package com.varanegar.vaslibrary.manager.operationReport;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.operationReport.OperationReportView;
import com.varanegar.vaslibrary.model.operationReport.OperationReportViewModel;
import com.varanegar.vaslibrary.model.operationReport.OperationReportViewModelRepository;

/**
 * Created by g.aliakbar on 22/04/2018.
 */

public class OperationReportViewManager extends BaseManager<OperationReportViewModel> {
    public OperationReportViewManager(@NonNull Context context) {
        super(context, new OperationReportViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(OperationReportView.OperationReportViewTbl).orderByDescending(OperationReportView.TotalAmount)
                .orderByDescending(OperationReportView.ReturnAddAmount);
        return query;
    }


}

