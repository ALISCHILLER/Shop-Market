package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetail;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetailModelRepository;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetail;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetailModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by e.hashemzadeh on 7/9/2021.
 */
public class CallInvoiceLineBatchQtyDetailManager extends BaseManager<CallInvoiceLineBatchQtyDetailModel> {
    public CallInvoiceLineBatchQtyDetailManager(@NonNull Context context) {
        super(context, new CallInvoiceLineBatchQtyDetailModelRepository());
    }

    public List<CallInvoiceLineBatchQtyDetailModel> getLineBatchQtyDetails(UUID CustomerCallOrderLineUniqueId) {
        Query query = new Query();
        query.from(From.table(CallInvoiceLineBatchQtyDetail.CallInvoiceLineBatchQtyDetailTbl)).whereAnd(Criteria.equals(CallInvoiceLineBatchQtyDetail.CustomerCallOrderLineUniqueId, CustomerCallOrderLineUniqueId));
        return getItems(query);
    }
}
