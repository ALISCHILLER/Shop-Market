package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customercallview.CustomerCallViewModel;
import com.varanegar.vaslibrary.model.customercallview.CustomerCallViewModelRepository;

/**
 * Created by A.Torabi on 10/10/2017.
 */

public class CustomerCallViewManager extends BaseManager<CustomerCallViewModel> {
    public CustomerCallViewManager(@NonNull Context context) {
        super(context, new CustomerCallViewModelRepository());
    }
}
