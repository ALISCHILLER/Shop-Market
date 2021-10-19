package com.varanegar.vaslibrary.manager.customercallmanager;

import androidx.annotation.NonNull;

import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 5/23/2018.
 */

public class CustomerCalls {
    public CustomerCalls(List<CustomerCallModel> calls) {
        for (CustomerCallModel callModel :
                calls) {
            if (map.containsKey(callModel.CallType)) {
                List<CustomerCallModel> callModels = map.get(callModel.CallType);
                callModels.add(callModel);
            } else {
                List<CustomerCallModel> callModels = new ArrayList<>();
                callModels.add(callModel);
                map.put(callModel.CallType, callModels);
            }
        }
    }

    private HashMap<CustomerCallType, List<CustomerCallModel>> map = new HashMap<>();

    @NonNull
    public List<CustomerCallModel> getCall(CustomerCallType callType) {
        if (map.containsKey(callType))
            return map.get(callType);
        else
            return new ArrayList<>();
    }

}
