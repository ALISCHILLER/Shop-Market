package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.evcstatutemanager.EvcStatuteProductGroupManager;
import com.varanegar.vaslibrary.manager.evcstatutemanager.EvcStatuteProductManager;
import com.varanegar.vaslibrary.manager.evcstatutemanager.EvcStatuteTemplateManager;
import com.varanegar.vaslibrary.model.customerproductprize.CustomerProductPrizeModel;
import com.varanegar.vaslibrary.model.customerproductprize.CustomerProductPrizeModelRepository;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductGroupModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteTemplateModel;
import com.varanegar.vaslibrary.model.product.ProductModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 9/18/2017.
 */

public class CustomerProductPrizeManager extends BaseManager<CustomerProductPrizeModel> {
    public CustomerProductPrizeManager(@NonNull Context context) {
        super(context, new CustomerProductPrizeModelRepository());
    }

    public void calculate(final UUID customerId) throws ValidationException, DbException {
        EvcStatuteTemplateManager evcStatuteTemplateManager = new EvcStatuteTemplateManager(getContext());
        final List<EvcStatuteTemplateModel> evcStatuteTemplateModels = evcStatuteTemplateManager.getValidTemplatesForCustomer(customerId);
        if (evcStatuteTemplateModels.size() == 0) {
            Timber.i("No evc template for customer id = " + customerId.toString());
            return;
        }

        deleteAll();
        HashMap<UUID, CustomerProductPrizeModel> prizeModels = new HashMap<>();
        for (EvcStatuteTemplateModel evcStatuteTemplateModel :
                evcStatuteTemplateModels) {
            EvcStatuteProductManager evcStatuteProductManager = new EvcStatuteProductManager(getContext());
            List<EvcStatuteProductModel> evcStatuteProductModels = evcStatuteProductManager.getItems(evcStatuteTemplateModel.UniqueId);
            for (EvcStatuteProductModel evcStatuteProductModel :
                    evcStatuteProductModels) {
                CustomerProductPrizeModel prizeModel = prizeModels.get(evcStatuteProductModel.ProductUniqueId);
                if (prizeModel == null)
                    prizeModel = new CustomerProductPrizeModel();
                prizeModel.UniqueId = UUID.randomUUID();
                prizeModel.ProductId = evcStatuteProductModel.ProductUniqueId;
                if (evcStatuteProductModel.Description != null)
                    prizeModel.Comment = prizeModel.Comment + "\n" + evcStatuteProductModel.Description;
                prizeModel.CustomerId = customerId;
                prizeModels.put(evcStatuteProductModel.ProductUniqueId, prizeModel);
            }
            EvcStatuteProductGroupManager evcStatuteProductGroupManager = new EvcStatuteProductGroupManager(getContext());
            List<EvcStatuteProductGroupModel> evcStatuteProductGroupModels = evcStatuteProductGroupManager.getItems(evcStatuteTemplateModel.UniqueId);
            for (EvcStatuteProductGroupModel evcStatuteProductGroupModel :
                    evcStatuteProductGroupModels) {
                ProductGroupManager productGroupViewManager = new ProductGroupManager(getContext());
                UUID[] subGroupIds = productGroupViewManager.getSubGroupIds(evcStatuteProductGroupModel.ProductGroupUniqueId, ProductType.isForSale);
                ProductManager productManager = new ProductManager(getContext());
                List<ProductModel> productModels = productManager.getItemsOfSubGroups(subGroupIds, true);
                for (ProductModel productModel : productModels) {
                    CustomerProductPrizeModel prizeModel = prizeModels.get(productModel.UniqueId);
                    if (prizeModel == null)
                        prizeModel = new CustomerProductPrizeModel();
                    prizeModel.UniqueId = UUID.randomUUID();
                    prizeModel.ProductId = productModel.UniqueId;
                    prizeModel.Comment = prizeModel.Comment + "\n" + evcStatuteProductGroupModel.Description;
                    prizeModel.CustomerId = customerId;
                    prizeModels.put(productModel.UniqueId, prizeModel);
                }
            }
        }

        Collection<CustomerProductPrizeModel> prizeModelList = prizeModels.values();
        if (prizeModelList.size() > 0) {
            insert(prizeModelList);
            Timber.i("Prize model calculated successfully for customer id = " + customerId);
        } else
            Timber.i("No Prize calculated for customer id " + customerId.toString());
    }
}
