package com.varanegar.vaslibrary.model.newmodel.locationconfirmmodel;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQty;

import java.util.UUID;

public class LocationConfirmTrackingManager  extends BaseManager<LocationConfirmTrackingModel> {

    public LocationConfirmTrackingManager(@NonNull Context context) {
        super(context, new LocationConfirmTrackingModelRepository());
    }


    public LocationConfirmTrackingModel getItem(UUID customerId){
        return getItem(new Query().from(LocationConfirmTracking.LocationConfirmTrackingTbl)
                .whereAnd(Criteria.equals(LocationConfirmTracking.UniqueId, customerId)));

    }


}
