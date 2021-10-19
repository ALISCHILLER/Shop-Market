package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customerCallOrderLinesItemStatutes.CustomerCallOrderLinesItemStatutesModel;
import com.varanegar.vaslibrary.model.customerCallOrderLinesItemStatutes.CustomerCallOrderLinesItemStatutesModelRepository;

/**
 * Created by A.Jafarzadeh on 8/8/2017.
 */

public class CustomerCallOrderLinesItemStatutesManager extends BaseManager<CustomerCallOrderLinesItemStatutesModel> {
    public CustomerCallOrderLinesItemStatutesManager(@NonNull Context context) {
        super(context, new CustomerCallOrderLinesItemStatutesModelRepository());
    }
}
