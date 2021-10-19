package com.varanegar.vaslibrary.webapi.target;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.vaslibrary.model.target.TargetMasterModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/13/2017.
 */

public class TargetMasterViewModel extends BaseModel{

    public String Title;

    public UUID TargetTypeUniqueId;

    public UUID TargetBaseUniqueId;

    public UUID AmountTypeUniqueId;

    public Date FromDate;

    public String FromPDate;

    public Date ToDate;

    public String ToPDate;

    public String SaleZoneUniqueIds;

    public String StateUniqueIds;

    public String CityUniqueIds;

    public String CustomerActivityUniqueIds;

    public String CustomerCategoryUniqueIds;

    public String CustomerLevelUniqueIds;

    public String CenterUniqueIds;

    public String SaleOfficeUniqueIds;

    public String ManufacturerUniqueIds;

    public String ProductMainGroupUniqueIds;
    public String ProductSubGroupUniqueIds;

    public List<TargetDetailViewModel> TargetDetails;

    public TargetMasterModel convert() {
        TargetMasterModel targetMasterViewModel = new TargetMasterModel();
        targetMasterViewModel.Title = Title;
        targetMasterViewModel.UniqueId = UniqueId;
        targetMasterViewModel.TargetTypeUniqueId = TargetTypeUniqueId;
        targetMasterViewModel.TargetBaseUniqueId = TargetBaseUniqueId;
        targetMasterViewModel.AmountTypeUniqueId = AmountTypeUniqueId;
        targetMasterViewModel.FromDate = FromDate;
        targetMasterViewModel.FromPDate = FromPDate;
        targetMasterViewModel.ToDate = ToDate;
        targetMasterViewModel.ToPDate = ToPDate;
        targetMasterViewModel.SaleZoneUniqueIds = SaleZoneUniqueIds;
        targetMasterViewModel.StateUniqueIds = StateUniqueIds;
        targetMasterViewModel.CityUniqueIds = CityUniqueIds;
        targetMasterViewModel.CustomerActivityUniqueIds = CustomerActivityUniqueIds;
        targetMasterViewModel.CustomerCategoryUniqueIds = CustomerCategoryUniqueIds;
        targetMasterViewModel.CustomerLevelUniqueIds = CustomerLevelUniqueIds;
        targetMasterViewModel.CenterUniqueIds = CenterUniqueIds;
        targetMasterViewModel.SaleOfficeUniqueIds = SaleOfficeUniqueIds;
        targetMasterViewModel.ManufacturerUniqueIds = ManufacturerUniqueIds;
        targetMasterViewModel.ProductMainGroupUniqueIds = ProductMainGroupUniqueIds;
        targetMasterViewModel.ProductSubGroupUniqueIds = ProductSubGroupUniqueIds;
        return targetMasterViewModel;
    }
}
