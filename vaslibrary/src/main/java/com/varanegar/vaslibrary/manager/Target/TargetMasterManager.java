package com.varanegar.vaslibrary.manager.Target;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.model.customer.Customer;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.target.TargetDetail;
import com.varanegar.vaslibrary.model.target.TargetMaster;
import com.varanegar.vaslibrary.model.target.TargetMasterModel;
import com.varanegar.vaslibrary.model.target.TargetMasterModelRepository;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */

public class TargetMasterManager extends BaseManager<TargetMasterModel> {
    public TargetMasterManager(@NonNull Context context) {
        super(context, new TargetMasterModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(TargetMaster.TargetMasterTbl);
        return query;
    }

    public static Query getFilterTargets( UUID dealerId, @Nullable UUID customerId) {
        if (customerId == null) {

            Query inerquery = new Query();
            inerquery.from(TargetDetail.TargetDetailTbl)
                    .whereAnd(Criteria.equalsColumn(TargetMaster.UniqueId, TargetDetail.TargetMasterUniqueId))
                    .whereAnd(Criteria.in(TargetDetail.CustomerUniqueId, new Query().from(Customer.CustomerTbl).select(Customer.UniqueId)).or(Criteria.equals(TargetDetail.PersonnelUniqueId, dealerId.toString())));
            Query query = new Query().from(TargetMaster.TargetMasterTbl).whereAnd(Criteria.exists(inerquery));
            Timber.d(query.toString());
            return query;
        } else {
            Query inerquery = new Query();
            inerquery.from(TargetDetail.TargetDetailTbl)
                    .whereAnd(Criteria.equalsColumn(TargetMaster.UniqueId, TargetDetail.TargetMasterUniqueId))
                    .whereAnd(Criteria.equals(TargetDetail.CustomerUniqueId, customerId.toString()));
            Query query = new Query().from(TargetMaster.TargetMasterTbl).whereAnd(Criteria.exists(inerquery));
            return query;
        }
    }

    public static Query getTaregetMaster(UUID targetMasterUUID) {
        Query query = new Query();
        query.from(TargetMaster.TargetMasterTbl).whereAnd(Criteria.equals(TargetMaster.UniqueId, targetMasterUUID));
        return query;
    }

}
