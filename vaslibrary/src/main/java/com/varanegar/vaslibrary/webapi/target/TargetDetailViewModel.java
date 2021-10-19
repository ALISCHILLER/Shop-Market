package com.varanegar.vaslibrary.webapi.target;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.target.TargetDetailModel;
import com.varanegar.vaslibrary.model.target.TargetProductGroupModel;
import com.varanegar.vaslibrary.model.target.TargetProductModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/13/2017.
 */

public class TargetDetailViewModel extends BaseModel {

    public UUID TargetMasterUniqueId;
    public UUID CustomerUniqueId;
    public UUID PersonnelUniqueId;

    public int CustomerCount;
    public int VisitCount;
    public int SuccessfulVisitCount;
    public int OrderCount;
    public int OrderItemCount;
    public Currency OrderAmount;

    public List<TargetProductModel> TargetProducts;
    public List<TargetProductGroupModel> TargetProductGroups;

    public TargetDetailModel convert() {
        TargetDetailModel targetDetailViewMode = new TargetDetailModel();
        targetDetailViewMode.UniqueId = UniqueId;
        targetDetailViewMode.TargetMasterUniqueId = TargetMasterUniqueId;
        targetDetailViewMode.CustomerUniqueId = CustomerUniqueId;
        targetDetailViewMode.PersonnelUniqueId = PersonnelUniqueId;
        targetDetailViewMode.CustomerCount = CustomerCount;
        targetDetailViewMode.VisitCount = VisitCount;
        targetDetailViewMode.SuccessfulVisitCount = SuccessfulVisitCount;
        targetDetailViewMode.OrderCount = OrderCount;
        targetDetailViewMode.OrderItemCount = OrderItemCount;
        targetDetailViewMode.OrderAmount = OrderAmount;
        return targetDetailViewMode;
    }

}