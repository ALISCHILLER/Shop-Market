package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.evcitemstatuessdscustomers.EVCItemStatuesCustomers;
import com.varanegar.vaslibrary.model.evcitemstatuessdscustomers.EVCItemStatuesCustomersModel;
import com.varanegar.vaslibrary.model.evcitemstatuessdscustomers.EVCItemStatuesCustomersModelRepository;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/17/2018.
 */

public class EVCItemStatuesCustomersManager extends BaseManager<EVCItemStatuesCustomersModel> {

    public EVCItemStatuesCustomersManager(@NonNull Context context) {
        super(context, new EVCItemStatuesCustomersModelRepository());
    }

    public void insertOrUpdateEVCItemStatus(List<EVCItemStatuesCustomersModel> evcItemStatuesCustomersModels, UUID customerId) throws DbException, ValidationException {
        if (evcItemStatuesCustomersModels != null && evcItemStatuesCustomersModels.size() > 0)
            insert(evcItemStatuesCustomersModels);
    }

    public void deleteEVCItemStatus(UUID customerId, UUID orderLineId) {
        try {
            delete(Criteria.equals(EVCItemStatuesCustomers.CustomerId, customerId).and(Criteria.equals(EVCItemStatuesCustomers.OrderLineId, orderLineId)));
        } catch (DbException e) {
            Timber.e(e);
        }
    }

    public List<EVCItemStatuesCustomersModel> getEVCItemStatus(UUID orderLineId) {
        Query query = new Query();
        query.from(EVCItemStatuesCustomers.EVCItemStatuesCustomersTbl).whereAnd(Criteria.equals(EVCItemStatuesCustomers.OrderLineId, orderLineId));
        return getItems(query);
    }

}
