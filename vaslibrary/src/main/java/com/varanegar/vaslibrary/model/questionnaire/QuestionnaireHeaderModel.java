package com.varanegar.vaslibrary.model.questionnaire;

import com.google.gson.annotations.Expose;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/3/2017.
 */
@Table
public class QuestionnaireHeaderModel extends BaseModel {
    @Column
    public String Title;
    @Column
    public UUID DemandTypeUniqueId;
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
}
