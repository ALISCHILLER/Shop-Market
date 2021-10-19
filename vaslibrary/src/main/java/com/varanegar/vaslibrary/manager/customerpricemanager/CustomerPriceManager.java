package com.varanegar.vaslibrary.manager.customerpricemanager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.customerprice.CustomerPrice;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModelRepository;

import java.util.List;
import java.util.UUID;

public class CustomerPriceManager extends BaseManager<CustomerPriceModel> {
    public static final UUID CustomizedPrice = UUID.fromString("1274ca61-b04f-4dee-bd39-aa75f840d088");

    public CustomerPriceManager(@NonNull Context context) {
        super(context, new CustomerPriceModelRepository());
    }

    public void saveCustomPrice(@NonNull UUID customerId, @NonNull UUID orderId, @NonNull UUID productId, Currency price, UUID priceId, Currency UserPrice) throws Exception {
        CustomerPriceModel priceModel = getItem(new Query().from(CustomerPrice.CustomerPriceTbl).whereAnd(Criteria.equals(CustomerPrice.CallOrderId, orderId.toString())
                .and(Criteria.equals(CustomerPrice.CustomerUniqueId, customerId.toString())
                        .and(Criteria.equals(CustomerPrice.ProductUniqueId, productId.toString())))));
        if (priceModel == null) {
            priceModel = new CustomerPriceModel();
            priceModel.UniqueId = UUID.randomUUID();
            priceModel.CallOrderId = orderId;
            priceModel.CustomerUniqueId = customerId;
            priceModel.ProductUniqueId = productId;
        }
        if (priceId == null)
            priceModel.PriceId = CustomizedPrice;
        else
            priceModel.PriceId = priceId;
        priceModel.Price = price;
        priceModel.UserPrice = UserPrice;

        insertOrUpdate(priceModel);
    }

    public List<CustomerPriceModel> getCustomizedPrices(@NonNull UUID customerId, @NonNull UUID callOrderId) {
        return getItems(new Query().from(CustomerPrice.CustomerPriceTbl).whereAnd(Criteria.equals(CustomerPrice.PriceId, CustomizedPrice.toString())
                .and(Criteria.equals(CustomerPrice.CustomerUniqueId, customerId.toString())
                        .and(Criteria.equals(CustomerPrice.CallOrderId, callOrderId.toString())))));
    }

    public CustomerPriceModel getProductPrice(@NonNull UUID customerId, @NonNull UUID callOrderId, @NonNull UUID productId) {
        return getItem(new Query().from(CustomerPrice.CustomerPriceTbl).whereAnd(Criteria.equals(CustomerPrice.CustomerUniqueId, customerId.toString())
                .and(Criteria.equals(CustomerPrice.CallOrderId, callOrderId.toString()))
                .and(Criteria.equals(CustomerPrice.ProductUniqueId, productId.toString()))));
    }
}
