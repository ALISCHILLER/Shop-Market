package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.dataforregister.CustomerDataForRegister;
import com.varanegar.vaslibrary.model.dataforregister.CustomerDataForRegisterModel;
import com.varanegar.vaslibrary.model.dataforregister.CustomerDataForRegisterModelRepository;

import java.util.List;
import java.util.UUID;

// NGT-4023 زر ماکارون
public class CustomerDataForRegisterManager extends BaseManager<CustomerDataForRegisterModel> {
    public CustomerDataForRegisterManager(@NonNull Context context) {
        super(context, new CustomerDataForRegisterModelRepository());
    }


    public List<CustomerDataForRegisterModel> getAll(UUID customerId) {
        return getItems(new Query().from(CustomerDataForRegister.CustomerDataForRegisterTbl).whereAnd(Criteria.equals(CustomerDataForRegister.CustomerId, customerId)));
    }
}
