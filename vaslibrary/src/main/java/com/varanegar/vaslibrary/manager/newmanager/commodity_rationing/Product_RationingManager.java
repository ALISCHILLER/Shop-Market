package com.varanegar.vaslibrary.manager.newmanager.commodity_rationing;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;

import java.util.UUID;

public class Product_RationingManager extends BaseManager<Product_RationingViewModel> {


    public Product_RationingManager(@NonNull Context context) {
        super(context, new Product_RationingViewModelRepository());
    }

    public  Product_RationingViewModel getProduct_Rationing(UUID productId){
        Query query = new Query();
        query.from(From.table(Product_RationingView.Product_RationingViewTbl))
                .whereAnd(Criteria.contains(Product_RationingView.productUniuqeId, String.valueOf(productId)));
        return getItem(query);
    }
}
