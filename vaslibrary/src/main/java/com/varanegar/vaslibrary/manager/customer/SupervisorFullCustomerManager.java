package com.varanegar.vaslibrary.manager.customer;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModel;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModelRepository;

/**
 * Created by A.Soleimani on 10/17/2021.
 */
public class SupervisorFullCustomerManager  extends BaseManager<SupervisorFullCustomerModel> {

    public SupervisorFullCustomerManager(@NonNull Context context) {
        super(context,  new SupervisorFullCustomerModelRepository());
    }
}
