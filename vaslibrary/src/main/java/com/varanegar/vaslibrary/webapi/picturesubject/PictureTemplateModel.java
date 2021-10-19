package com.varanegar.vaslibrary.webapi.picturesubject;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateDetailModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateHeaderModel;

import java.util.Date;
import java.util.List;

/**
 * Created by A.Jafarzadeh on 10/16/2017.
 */

public class PictureTemplateModel extends BaseModel {
    public Date FromDate;
    public Date ToDate;
    public String CenterUniqueIds;
    @SerializedName("saleAreaUniqueIds") //we receive AreaId as ZoneId from Webservice
    public String SaleZoneUniqueIds;
    public String StateUniqueIds;
    public String CityUniqueIds;
    public String CustomerActivityUniqueIds;
    public String CustomerCategoryUniqueIds;
    public String CustomerLevelUniqueIds;
    public String SaleOfficeUniqueIds;
    public String FromPDate;
    public String ToPDate;

    public List<PictureTemplateDetailModel> PictureTemplateDetails;

    public PictureTemplateHeaderModel convert() {
        PictureTemplateHeaderModel ptvmh = new PictureTemplateHeaderModel();
        ptvmh.UniqueId = UniqueId;
        ptvmh.FromDate = FromDate;
        ptvmh.ToDate = ToDate;
        ptvmh.CenterUniqueIds = CenterUniqueIds;
        ptvmh.SaleZoneUniqueIds = SaleZoneUniqueIds;
        ptvmh.StateUniqueIds = StateUniqueIds;
        ptvmh.CityUniqueIds = CityUniqueIds;
        ptvmh.CustomerActivityUniqueIds = CustomerActivityUniqueIds;
        ptvmh.CustomerCategoryUniqueIds = CustomerCategoryUniqueIds;
        ptvmh.CustomerLevelUniqueIds = CustomerLevelUniqueIds;
        ptvmh.SaleOfficeUniqueIds = SaleOfficeUniqueIds;
        ptvmh.isRemoved = isRemoved;
        ptvmh.FromPDate = FromPDate;
        ptvmh.ToPDate = ToPDate;
        return ptvmh;
    }
}
