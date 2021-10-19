package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.TextView;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.customer.Customer;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerCall;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerCallModel;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerCallModelRepository;

import java.util.List;

/**
 * Created by A.Jafarzadeh on 2/26/2018.
 */

public class DistributionCustomerCallManager extends BaseManager<DistributionCustomerCallModel> {
    public DistributionCustomerCallManager(@NonNull Context context) {
        super(context, new DistributionCustomerCallModelRepository());

    }

    public List<DistributionCustomerCallModel> getAll() {
        Query query = new Query();
        query.from(DistributionCustomerCall.DistributionCustomerCallTbl);
        return getItems(query);
    }

    public List<DistributionCustomerCallModel> getAllWithGroupBy() {
        Query query = new Query();
        query.from(DistributionCustomerCall.DistributionCustomerCallTbl).groupBy(DistributionCustomerCall.DistributionNo);
        return getItems(query);
    }

    public String getDistNumbers() {
        List<DistributionCustomerCallModel> customerCallModels = getAllWithGroupBy();
        String distNumbers = "";
        if (customerCallModels.size() > 0) {
            for (DistributionCustomerCallModel customerCallModel : customerCallModels) {
                if (distNumbers.equals(""))
                    distNumbers = customerCallModel.DistributionNo;
                else
                    distNumbers = distNumbers + " - " + customerCallModel.DistributionNo;
            }
        }
        return distNumbers;
    }


}
