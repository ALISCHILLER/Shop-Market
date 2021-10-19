package com.varanegar.vaslibrary.webapi.questionnaire;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHeaderModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 10/21/2017.
 */

public class QuestionnaireHeaderViewModel extends BaseModel {
    public String Title;
    public UUID DemandTypeUniqueId;
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
    public List<QuestionnaireLineViewModel> questionnaireLines;
    public QuestionnaireHeaderModel convert(){
        QuestionnaireHeaderModel questionnaireHeaderModel = new QuestionnaireHeaderModel();
        questionnaireHeaderModel.UniqueId = UniqueId;
        questionnaireHeaderModel.Title = Title;
        questionnaireHeaderModel.DemandTypeUniqueId = DemandTypeUniqueId;
        questionnaireHeaderModel.FromDate = FromDate;
        questionnaireHeaderModel.ToDate = ToDate;
        questionnaireHeaderModel.CenterUniqueIds = CenterUniqueIds;
        questionnaireHeaderModel.SaleZoneUniqueIds = SaleZoneUniqueIds;
        questionnaireHeaderModel.StateUniqueIds = StateUniqueIds;
        questionnaireHeaderModel.CityUniqueIds = CityUniqueIds;
        questionnaireHeaderModel.CustomerActivityUniqueIds = CustomerActivityUniqueIds;
        questionnaireHeaderModel.CustomerCategoryUniqueIds = CustomerCategoryUniqueIds;
        questionnaireHeaderModel.CustomerLevelUniqueIds = CustomerLevelUniqueIds;
        questionnaireHeaderModel.SaleOfficeUniqueIds = SaleOfficeUniqueIds;
        return questionnaireHeaderModel;
    }
}
