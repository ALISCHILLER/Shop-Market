package com.varanegar.vaslibrary.manager.customerpromotionpricemanager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.vaslibrary.model.customerpromotionprice.CustomerPromotionPriceModel;
import com.varanegar.vaslibrary.model.customerpromotionprice.CustomerPromotionPriceModelRepository;


/**
 * Created by e.hashemzadeh on 10/12/2021.
 */

public class CustomerPromotionPriceManager extends BaseManager<CustomerPromotionPriceModel> {
    public CustomerPromotionPriceManager(@NonNull Context context) {
        super(context, new CustomerPromotionPriceModelRepository());
    }
}
