package com.varanegar.vaslibrary.manager.emphaticitems;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.model.customeremphaticproduct.CustomerEmphaticProduct;
import com.varanegar.vaslibrary.model.customeremphaticproduct.CustomerEmphaticProductModel;
import com.varanegar.vaslibrary.model.customeremphaticproduct.CustomerEmphaticProductModelRepository;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCount;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;
import com.varanegar.vaslibrary.model.product.Product;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/16/2017.
 */

public class CustomerEmphaticProductManager extends BaseManager<CustomerEmphaticProductModel> {
    public CustomerEmphaticProductManager(@NonNull Context context) {
        super(context, new CustomerEmphaticProductModelRepository());
    }

    public List<CustomerEmphaticProductModel> getAll(UUID customerId) {
        Query query = new Query();
        query.from(From.table(CustomerEmphaticProduct.CustomerEmphaticProductTbl).innerJoin(Product.ProductTbl)
                .on(CustomerEmphaticProduct.ProductId, Product.UniqueId))
                .whereAnd(Criteria.equals(CustomerEmphaticProduct.CustomerId, customerId.toString())
                        .and(Criteria.equals(Product.IsForSale, true)
                        .and(Criteria.notEquals(CustomerEmphaticProduct.TypeLawUniqueId , EmphaticProductManager.PACKAGE))));
        return getItems(query);
    }

//    public List<CustomerEmphaticProductModel> getAll(UUID TypeId, UUID customerId) {
//        Query query = new Query();
//        query.from(CustomerEmphaticProduct.CustomerEmphaticProductTbl)
//                .whereAnd(Criteria.equals(CustomerEmphaticProduct.CustomerId, customerId.toString())
//                        .and(Criteria.equals(CustomerEmphaticProduct.TypeId, TypeId.toString())));
//        return getItems(query);
//    }
//
//    public List<CustomerEmphaticProductModel> getAllExistingItems(UUID TypeId, UUID customerId) {
//        Query query = new Query();
//        query.from(From.table(CustomerEmphaticProduct.CustomerEmphaticProductTbl).innerJoin(Product.ProductTbl)
//                .on(CustomerEmphaticProduct.ProductId, Product.UniqueId))
//                .whereAnd(Criteria.equals(CustomerEmphaticProduct.CustomerId, customerId.toString())
//                        .and(Criteria.equals(CustomerEmphaticProduct.TypeId, TypeId.toString())));
//        return getItems(query);
//    }

    public void checkCustomerEmphaticItems(UUID customerId) throws EmphaticItemNotFoundException {
        Query query = new Query();
        query.from(From.table(CustomerEmphaticProduct.CustomerEmphaticProductTbl).innerJoin(Product.ProductTbl)
                .on(CustomerEmphaticProduct.ProductId, Product.UniqueId))
                .whereAnd(Criteria.equals(CustomerEmphaticProduct.CustomerId, customerId.toString())
                        .and(Criteria.equals(CustomerEmphaticProduct.Type, EmphasisType.Deterrent))
                        .and(Criteria.equals(Product.IsForSale, false)));

        List<CustomerEmphaticProductModel> invalidItems = getItems(query);
        if (invalidItems.size() > 0) {
            List<String> notFoundIds = Linq.map(invalidItems, new Linq.Map<CustomerEmphaticProductModel, String>() {
                @Override
                public String run(CustomerEmphaticProductModel item) {
                    return item.ProductId.toString();
                }
            });
            Query q = new Query().from(EmphaticProductCount.EmphaticProductCountTbl);
            q.whereAnd(Criteria.in(EmphaticProductCount.ProductId, notFoundIds));
            EmphaticProductCountManager emphaticProductCountManager = new EmphaticProductCountManager(getContext());
            List<EmphaticProductCountModel> items = emphaticProductCountManager.getItems(q);
            throw new EmphaticItemNotFoundException(items);
        }
    }
}
