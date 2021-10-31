package com.varanegar.vaslibrary.manager.customer;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModel;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModelRepository;

/**
 * Created by e.hashemzadeh on 10/17/2021.
 */
public class SupervisorCustomerManager extends BaseManager<SupervisorCustomerModel> {
    public SupervisorCustomerManager(@NonNull Context context) {
        super(context, new SupervisorCustomerModelRepository());
    }
}
