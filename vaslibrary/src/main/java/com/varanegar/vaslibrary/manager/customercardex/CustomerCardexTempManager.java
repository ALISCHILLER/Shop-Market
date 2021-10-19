package com.varanegar.vaslibrary.manager.customercardex;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardex;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexTemp;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexTempModel;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexTempModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/18/2018.
 */

public class CustomerCardexTempManager extends BaseManager<CustomerCardexTempModel> {
    public CustomerCardexTempManager(@NonNull Context context) {
        super(context, new CustomerCardexTempModelRepository());
    }

    public List<CustomerCardexTempModel> getCustomerItems(UUID customerId) {
        Query query = new Query();
        query.from(CustomerCardexTemp.CustomerCardexTempTbl).whereAnd(Criteria.equals(CustomerCardexTemp.CustomerUniqueId, customerId.toString()));
        return getItems(query);
    }

    public static Query getAll(UUID key, boolean orderBy) {
        Query query = new Query();
        query.from(CustomerCardexTemp.CustomerCardexTempTbl).whereAnd(Criteria.equals(CustomerCardexTemp.CustomerUniqueId, key));
        if (orderBy)
            query.orderByAscending(CustomerCardexTemp.SortId);
        return query;
    }

}
