package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventory;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryModel;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryModelRepository;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */

public class CustomerInventoryManager extends BaseManager<CustomerInventoryModel> {
    public CustomerInventoryManager(@NonNull Context context) {
        super(context, new CustomerInventoryModelRepository());
    }

    public List<CustomerInventoryModel> getLines(UUID customerId) {
        Query query = new Query();
        query.from(CustomerInventory.CustomerInventoryTbl)
                .whereAnd(Criteria.equals(CustomerInventory.CustomerId, customerId.toString()));
        return getItems(query);
    }

    public CustomerInventoryModel getLine(UUID productId, UUID customerId) {
        Query query = new Query().from(CustomerInventory.CustomerInventoryTbl)
                .whereAnd(Criteria.equals(CustomerInventory.ProductId, productId.toString())
                        .and(Criteria.equals(CustomerInventory.CustomerId, customerId.toString())));
        return getItem(query);
    }

    public void add(final CustomerInventoryModel model, final List<DiscreteUnit> units) throws ValidationException, DbException {
        insertOrUpdate(model);
        CustomerInventoryQtyManager qtyManager = new CustomerInventoryQtyManager(getContext());
        qtyManager.addOrUpdate(model.UniqueId, units);
    }

    public void deleteLines(UUID customerId) throws DbException {
        delete(Criteria.equals(CustomerInventory.CustomerId, customerId.toString()));
    }
}
