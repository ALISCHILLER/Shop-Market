package com.varanegar.vaslibrary.model.emphaticproduct;

import androidx.annotation.ColorInt;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2017.
 */
@Table
public class EmphaticProductModel extends BaseModel {
    @Column
    public Date FromDate;
    @Column
    public Date ToDate;
    @Column
    public UUID EmphasisProductErrorTypeId;
    @Column
    public UUID DcId;
    @Column
    public UUID SaleZoneId;
    @Column
    public UUID StateId;
    @Column
    public UUID CityId;
    @Column
    public UUID CustomerActivityId;
    @Column
    public UUID CustomerCategoryId;
    @Column
    public UUID CustomerLevelId;
    @Column
    public UUID SaleOfficeId;
    @Column
    public UUID ManufacturerId;
    @Column
    public int WarningDay;
    @Column
    public int DangerDay;
    @Column
    public UUID TypeLawUniqueId;
    @Column
    public String Title;
    @Column
    public BigDecimal PackageCount;
    @Column
    public UUID saleAreaUniqueId;
    public List<EmphaticProductCountModel> emphasisProductDetails;
    @Column
    public boolean IsEmphasis;
}
