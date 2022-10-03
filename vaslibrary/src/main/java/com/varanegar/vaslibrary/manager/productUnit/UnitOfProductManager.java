package com.varanegar.vaslibrary.manager.productUnit;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.vaslibrary.model.productUnit.ProductUnit;
import com.varanegar.vaslibrary.model.productUnit.UnitOfProduct;
import com.varanegar.vaslibrary.model.productUnit.UnitOfProductModel;
import com.varanegar.vaslibrary.model.productUnit.UnitOfProductModelRepository;
import com.varanegar.vaslibrary.model.unit.Unit;
import com.varanegar.vaslibrary.model.unit.UnitModelRepository;

import java.util.List;
import java.util.UUID;

public class UnitOfProductManager extends BaseManager<UnitOfProductModel> {
    public UnitOfProductManager(@NonNull Context context) {
        super(context, new UnitOfProductModelRepository());
    }


    public List<UnitOfProductModel> getUnitsOfProduct(UUID productId) {
        Query query = new Query();
        query.select(
                        Projection.column(Unit.UniqueId).as("UniqueId"),
                        Projection.column(Unit.UnitName).as("UnitName"),
                        Projection.column(Unit.BackOfficeId).as("BackOfficeId"),
                        Projection.column(ProductUnit.UniqueId).as("productUnitId"),
                        Projection.column(ProductUnit.ConvertFactor).as("ConvertFactor"),
                        Projection.column(ProductUnit.IsDefault).as("IsDefault"),
                        Projection.column(ProductUnit.IsForSale).as("IsForSale"),
                        Projection.column(ProductUnit.IsReturnDefault).as("IsReturnDefault"),
                        Projection.column(ProductUnit.IsForReturn).as("IsForReturn"))
                .from(From.table(Unit.UnitTbl).innerJoin(From.table(ProductUnit.ProductUnitTbl))
                        .on(Unit.UniqueId, ProductUnit.UnitId).onAnd(Criteria.equals(ProductUnit.ProductId, productId)));
        Query query2 = new Query().from(From.subQuery(query).as("UnitOfProduct"));
        return getItems(query2);
    }

}
