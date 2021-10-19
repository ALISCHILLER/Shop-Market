package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryQty;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryQtyModel;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryQtyModelRepository;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */

public class CustomerInventoryQtyManager extends BaseManager<CustomerInventoryQtyModel> {
    public CustomerInventoryQtyManager(@NonNull Context context) {
        super(context, new CustomerInventoryQtyModelRepository());
    }

    public void addOrUpdate(UUID customerInventoryId, List<DiscreteUnit> qtys) throws ValidationException, DbException {
        Query query = new Query();
        query.from(CustomerInventoryQty.CustomerInventoryQtyTbl)
                .whereAnd(Criteria.equals(CustomerInventoryQty.CustomerInventoryId, customerInventoryId));
        List<CustomerInventoryQtyModel> oldQtys = getItems(query);
        List<CustomerInventoryQtyModel> list = new ArrayList<>();
        for (final DiscreteUnit unit :
                qtys) {
            CustomerInventoryQtyModel qty = Linq.findFirst(oldQtys, new Linq.Criteria<CustomerInventoryQtyModel>() {
                @Override
                public boolean run(CustomerInventoryQtyModel item) {
                    return item.ProductUnitId.equals(unit.ProductUnitId);
                }
            });
            if (qty == null) {
                qty = new CustomerInventoryQtyModel();
                qty.UniqueId = UUID.randomUUID();
                qty.ProductUnitId = unit.ProductUnitId;
                qty.CustomerInventoryId = customerInventoryId;
            }
            qty.Qty = unit.getQty();
            list.add(qty);
        }
        insertOrUpdate(list);
    }

    public List<CustomerInventoryQtyModel> getLines(UUID customerInventoryId) {
        Query query = new Query();
        query.from(CustomerInventoryQty.CustomerInventoryQtyTbl)
                .whereAnd(Criteria.equals(CustomerInventoryQty.CustomerInventoryId, customerInventoryId.toString()));
        return getItems(query);
    }

    public void deleteLines(UUID customerInventoryId) throws DbException {
        delete(Criteria.equals(CustomerInventoryQty.CustomerInventoryId, customerInventoryId.toString()));
    }
}
