package com.varanegar.vaslibrary.model.evcstatute;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.List;

/**
 * Created by A.Torabi on 11/20/2017.
 */
@Table
public class EvcStatuteTemplateModel extends BaseModel {
    @Column
    public String Title;
    @Column
    public String Description;
    @Column
    public Date FromDate;
    @Column
    public Date ToDate;
    @Column
    public String CenterUniqueIds;
    @SerializedName("saleAreaUniqueIds") //we receive AreaId as ZoneId from Webservice
    @Column
    public String SaleZoneUniqueIds;
    @Column
    public String StateUniqueIds;
    @Column
    public String CityUniqueIds;
    @Column
    public String CustomerActivityUniqueIds;
    @Column
    public String CustomerCategoryUniqueIds;
    @Column
    public String CustomerLevelUniqueIds;
    @Column
    public String SaleOfficeUniqueIds;
    public List<EvcStatuteProductModel> EvcStatuteProducts;
    public List<EvcStatuteProductGroupModel> EvcStatuteProductGroups;
}
