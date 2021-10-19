package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreview;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/19/2018.
 */

public class CustomerCallOrderPreviewManager extends BaseManager<CustomerCallOrderPreviewModel> {
    public CustomerCallOrderPreviewManager(@NonNull Context context) {
        super(context, new CustomerCallOrderPreviewModelRepository());
    }

    public List<CustomerCallOrderPreviewModel> getCustomerCallOrders(UUID customerId) {
        Query query = new Query();
        query.from(CustomerCallOrderPreview.CustomerCallOrderPreviewTbl).whereAnd(Criteria.equals(CustomerCallOrderPreview.CustomerUniqueId, customerId.toString()));
        return getItems(query);
    }
}
