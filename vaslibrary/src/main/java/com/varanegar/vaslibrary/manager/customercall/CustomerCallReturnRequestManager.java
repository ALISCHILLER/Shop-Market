package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnRequest;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnRequestModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnRequestModelRepository;
import com.varanegar.vaslibrary.model.returnType.ReturnType;

import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 3/29/2017.
 */

public class CustomerCallReturnRequestManager extends BaseManager<CustomerCallReturnRequestModel> {
    public CustomerCallReturnRequestManager(Context context) {
        super(context, new CustomerCallReturnRequestModelRepository());
    }

    public List<CustomerCallReturnRequestModel> getCustomerCallReturns(UUID customerUniqueId, @Nullable Boolean withRef) {
        Query query = new Query();
        query.from(CustomerCallReturnRequest.CustomerCallReturnRequestTbl).whereAnd(Criteria.equals(CustomerCallReturnRequest.CustomerUniqueId, customerUniqueId.toString()));
        if (withRef != null)
        {
            if (withRef)
                query.whereAnd(Criteria.equals(CustomerCallReturnRequest.ReturnTypeUniqueId, ReturnType.WithRef));
            else
                query.whereAnd(Criteria.equals(CustomerCallReturnRequest.ReturnTypeUniqueId, ReturnType.WithoutRef));
        }
        return getItems(query);
    }

    public CustomerCallReturnRequestModel getCustomerCallReturn(UUID uniqueId) {
        Query query = new Query();
        query.from(CustomerCallReturnRequest.CustomerCallReturnRequestTbl).whereAnd(Criteria.equals(CustomerCallReturnRequest.CustomerUniqueId, uniqueId.toString()));
        return getItem(query);
    }


    public List<CustomerCallReturnRequestModel> getAll() {
        Query query = new Query();
        query.from(CustomerCallReturnRequest.CustomerCallReturnRequestTbl);
        return getItems(query);
    }
}






