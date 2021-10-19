package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPrice;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModel;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/24/2018.
 */

public class CustomerBatchPriceManager extends BaseManager<CustomerBatchPriceModel> {
    public CustomerBatchPriceManager(@NonNull Context context) {
        super(context, new CustomerBatchPriceModelRepository());
    }

    public List<CustomerBatchPriceModel> getCustomerBatchPrice(UUID customerUniqueId) {
        Query query = new Query();
        query.from(CustomerBatchPrice.CustomerBatchPriceTbl).whereAnd(Criteria.equals(CustomerBatchPrice.CustomerUniqueId, customerUniqueId.toString()));
        return getItems(query);
    }
}
