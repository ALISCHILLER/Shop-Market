package com.varanegar.vaslibrary.manager.emphaticitems;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.customeremphaticproduct.CustomerEmphaticProductModel;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.customeremphaticproductview.CustomerEmphaticProductView;
import com.varanegar.vaslibrary.model.customeremphaticproductview.CustomerEmphaticProductViewModel;
import com.varanegar.vaslibrary.model.customeremphaticproductview.CustomerEmphaticProductViewModelRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/16/2017.
 */

public class CustomerEmphaticProductViewManager extends BaseManager<CustomerEmphaticProductViewModel> {
    public CustomerEmphaticProductViewManager(@NonNull Context context) {
        super(context, new CustomerEmphaticProductViewModelRepository());
    }

    public void calcEmphaticProducts(final UUID customerId) throws ValidationException, DbException {
        Date date = new Date();
        Query query = new Query();
        query.from(CustomerEmphaticProductView.CustomerEmphaticProductViewTbl)
                .whereAnd(Criteria.valueBetween(date.getTime(), Projection.ifNull(Projection.column(CustomerEmphaticProductView.FromDate), "'" + date.getTime() + "'"), Projection.ifNull(Projection.column(CustomerEmphaticProductView.ToDate), "'" + date.getTime() + "'"))
                        .and(Criteria.equals(CustomerEmphaticProductView.CustomerId, customerId.toString())));
        final List<CustomerEmphaticProductViewModel> items = getItems(query);
        if (items.size() == 0)
            return;
        final CustomerEmphaticProductManager manager = new CustomerEmphaticProductManager(getContext());
        manager.deleteAll();
        List<CustomerEmphaticProductModel> models = Linq.map(items, new Linq.Map<CustomerEmphaticProductViewModel, CustomerEmphaticProductModel>() {
            @Override
            public CustomerEmphaticProductModel run(CustomerEmphaticProductViewModel item) {
                CustomerEmphaticProductModel model = new CustomerEmphaticProductModel();
                model.UniqueId = UUID.randomUUID();
                model.ProductCount = item.ProductCount;
                model.ProductId = item.ProductId;
                if (item.TypeId.equals(EmphasisProductErrorTypeId.DETERRENT))
                    model.Type = EmphasisType.Deterrent;
                else if (item.TypeId.equals(EmphasisProductErrorTypeId.WARNING))
                    model.Type = EmphasisType.Warning;
                else
                    model.Type = EmphasisType.Suggestion;
                model.CustomerId = customerId;
                model.WarningDate = item.WarningDate;
                model.DangerDate = item.DangerDate;
                model.EmphasisRuleId = item.RuleId;
                model.TypeLawUniqueId = item.TypeLawUniqueId;
                return model;
            }
        });
        long affectedRows = manager.insert(models);
        Timber.i(affectedRows +" Emphatic items for customer calculated successfully");


    }
}
