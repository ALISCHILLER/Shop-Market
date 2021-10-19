package com.varanegar.vaslibrary.model.picturesubject;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;

/**
 * Created by A.Jafarzadeh on 10/16/2017.
 */
@Table
public class PictureTemplateHeaderModel extends BaseModel {
    @Column
    public Date FromDate;
    @Column
    public Date ToDate;
    @Column
    public String CenterUniqueIds;
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
    @Column
    public String FromPDate;
    @Column
    public String ToPDate;
}
