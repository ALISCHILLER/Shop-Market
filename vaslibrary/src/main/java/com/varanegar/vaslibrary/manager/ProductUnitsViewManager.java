package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsView;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModelRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 5/22/2018.
 */

public class ProductUnitsViewManager extends BaseManager<ProductUnitsViewModel> {
    public ProductUnitsViewManager(@NonNull Context context) {
        super(context, new ProductUnitsViewModelRepository());
    }

    public HashMap<UUID, ProductUnitsViewModel> getProductsUnits() {
        HashMap<UUID, ProductUnitsViewModel> hashMap = new HashMap<>();
        List<ProductUnitsViewModel> items = getItems(new Query().from(ProductUnitsView.ProductUnitsViewTbl));
        for (ProductUnitsViewModel productUnitsViewModel :
                items) {
            hashMap.put(productUnitsViewModel.UniqueId, productUnitsViewModel);
        }
        return hashMap;
    }
}
