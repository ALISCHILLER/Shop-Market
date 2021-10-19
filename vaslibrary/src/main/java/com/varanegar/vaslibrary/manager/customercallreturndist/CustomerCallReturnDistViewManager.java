package com.varanegar.vaslibrary.manager.customercallreturndist;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.customercallreturndistview.CustomerCallReturnDistView;
import com.varanegar.vaslibrary.model.customercallreturndistview.CustomerCallReturnDistViewModel;
import com.varanegar.vaslibrary.model.customercallreturndistview.CustomerCallReturnDistViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 3/2/2019.
 */

public class CustomerCallReturnDistViewManager extends BaseManager<CustomerCallReturnDistViewModel> {
    public CustomerCallReturnDistViewManager(@NonNull Context context) {
        super(context, new CustomerCallReturnDistViewModelRepository());
    }

    public List<CustomerCallReturnDistViewModel> getLines(UUID customerId) {
        Query query = new Query().from(CustomerCallReturnDistView.CustomerCallReturnDistViewTbl).whereAnd(Criteria.equals(CustomerCallReturnDistView.CustomerUniqueId,customerId.toString()));
        return getItems(query);
    }
}
