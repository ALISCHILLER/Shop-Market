package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportView;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportViewModel;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportViewModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/19/2017.
 */

public class OldInvoiceDetailReportViewManager extends BaseManager<OldInvoiceDetailReportViewModel>
{
    public OldInvoiceDetailReportViewManager(@NonNull Context context)
    {
        super(context, new OldInvoiceDetailReportViewModelRepository());
    }

    public static Query getAll(UUID customerId)
    {
        Query query = new Query();
        query.from(OldInvoiceDetailReportView.OldInvoiceDetailReportViewTbl).whereAnd(Criteria.equals(OldInvoiceDetailReportView.CustomerId, customerId.toString()));
        return query;
    }

    public static Query getAll(UUID customerId, UUID saleId)
    {
        Query query = new Query();
        query.from(OldInvoiceDetailReportView.OldInvoiceDetailReportViewTbl).whereAnd(Criteria.equals(OldInvoiceDetailReportView.CustomerId, customerId.toString())).whereAnd(Criteria.equals(OldInvoiceDetailReportView.SaleId, saleId.toString()));
        return query;
    }
}
