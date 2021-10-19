package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customerCallReturnLinesWithPromo.CustomerCallReturnLinesWithPromoModel;
import com.varanegar.vaslibrary.model.customerCallReturnLinesWithPromo.CustomerCallReturnLinesWithPromoModelRepository;

/**
 * Created by A.Jafarzadeh on 8/9/2017.
 */

public class CustomerCallReturnLinesWithPromoManager extends BaseManager<CustomerCallReturnLinesWithPromoModel> {
    public CustomerCallReturnLinesWithPromoManager(@NonNull Context context) {
        super(context, new CustomerCallReturnLinesWithPromoModelRepository());
    }
}
