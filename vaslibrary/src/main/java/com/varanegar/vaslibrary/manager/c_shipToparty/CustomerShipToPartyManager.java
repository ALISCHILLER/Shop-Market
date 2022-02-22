package com.varanegar.vaslibrary.manager.c_shipToparty;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;

public class CustomerShipToPartyManager extends BaseManager<CustomerShipToPartyModel> {
    public CustomerShipToPartyManager(@NonNull Context context) {
        super(context, new CustomerShipToPartyModelRepository());
    }


}
