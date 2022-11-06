package com.varanegar.vaslibrary.manager.checkCustomerCreditmanger;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModelRepository;

public class CheckCustomerCreditManager extends BaseManager<CheckCustomerCreditModel> {
    public CheckCustomerCreditManager(@NonNull Context context) {
        super(context, new CheckCustomerCreditModelRepository());
    }


    



}
