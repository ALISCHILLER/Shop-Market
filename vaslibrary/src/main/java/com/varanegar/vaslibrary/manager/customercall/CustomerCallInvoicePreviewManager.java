package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoicePreview;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoicePreviewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoicePreviewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/19/2018.
 */

public class CustomerCallInvoicePreviewManager extends BaseManager<CustomerCallInvoicePreviewModel> {
    public CustomerCallInvoicePreviewManager(@NonNull Context context) {
        super(context, new CustomerCallInvoicePreviewModelRepository());
    }

    public List<CustomerCallInvoicePreviewModel> getCustomerCallOrders(UUID customerId) {
        Query query = new Query();
        query.from(CustomerCallInvoicePreview.CustomerCallInvoicePreviewTbl).whereAnd(Criteria.equals(CustomerCallInvoicePreview.CustomerUniqueId, customerId.toString()));
        return getItems(query);
    }
}
