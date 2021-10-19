package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerPriceModel;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerPriceModelRepository;

/**
 * Created by A.Torabi on 9/25/2018.
 */

public class DistributionCustomerPriceManager extends BaseManager<DistributionCustomerPriceModel> {
    public DistributionCustomerPriceManager(@NonNull Context context) {
        super(context, new DistributionCustomerPriceModelRepository());
    }
}
