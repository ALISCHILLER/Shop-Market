package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.CallInvoiceLine;
import com.varanegar.vaslibrary.model.call.CallInvoiceLineModel;
import com.varanegar.vaslibrary.model.call.CallInvoiceLineModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 28/02/2017.
 */

public class CallInvoiceLineManager extends BaseManager<CallInvoiceLineModel> {
    public CallInvoiceLineManager(Context context) {
        super(context, new CallInvoiceLineModelRepository());
    }

    public List<CallInvoiceLineModel> getLines(@NonNull UUID orderId) {
        Query query = new Query();
        query.from(CallInvoiceLine.CallInvoiceLineTbl).whereAnd(Criteria.equals(CallInvoiceLine.OrderUniqueId, orderId.toString()));
        return getItems(query);
    }

}
