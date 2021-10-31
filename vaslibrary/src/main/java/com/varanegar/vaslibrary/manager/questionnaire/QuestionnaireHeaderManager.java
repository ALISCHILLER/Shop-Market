package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customer.SupervisorCustomerManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHeader;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHeaderModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHeaderModelRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/31/2017.
 */

public class QuestionnaireHeaderManager extends BaseManager<QuestionnaireHeaderModel> {
    public QuestionnaireHeaderManager(@NonNull Context context) {
        super(context, new QuestionnaireHeaderModelRepository());
    }

    public List<QuestionnaireHeaderModel> getValidTemplatesForCustomer(UUID customerId) {
        CustomerManager customerManager = new CustomerManager(getContext());
        CustomerModel customer = customerManager.getItem(customerId);
        if (customer == null)
            throw new NullPointerException("CustomerId is not valid");
        String saleOfficeId = SysConfigManager.getValue(new SysConfigManager(getContext()).read(ConfigKey.SaleOfficeId, SysConfigManager.cloud));
        Query query = new Query();
        query.from(QuestionnaireHeader.QuestionnaireHeaderTbl)
                .whereAnd(Criteria.contains(QuestionnaireHeader.CenterUniqueIds, customer.CenterId == null ? UUID.randomUUID().toString() : customer.CenterId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CenterUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CenterUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CityUniqueIds, customer.CityId == null ? UUID.randomUUID().toString() : customer.CityId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CityUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CityUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CustomerActivityUniqueIds, customer.CustomerActivityId == null ? UUID.randomUUID().toString() : customer.CustomerActivityId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CustomerActivityUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CustomerActivityUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CustomerCategoryUniqueIds, customer.CustomerCategoryId == null ? UUID.randomUUID().toString() : customer.CustomerCategoryId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CustomerCategoryUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CustomerCategoryUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CustomerLevelUniqueIds, customer.CustomerLevelId == null ? UUID.randomUUID().toString() : customer.CustomerLevelId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CustomerLevelUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CustomerLevelUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.SaleOfficeUniqueIds, saleOfficeId == null ? UUID.randomUUID().toString() : saleOfficeId)
                        .or(Criteria.isNull(QuestionnaireHeader.SaleOfficeUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.SaleOfficeUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.SaleZoneUniqueIds, customer.AreaId == null ? UUID.randomUUID().toString() : customer.AreaId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.SaleZoneUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.SaleZoneUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.StateUniqueIds, customer.StateId == null ? UUID.randomUUID().toString() : customer.StateId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.StateUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.StateUniqueIds, "")))
                .whereAnd(Criteria.lesserThan(QuestionnaireHeader.FromDate, new Date())
                        .or(Criteria.isNull(QuestionnaireHeader.FromDate)))
                .whereAnd(Criteria.greaterThan(QuestionnaireHeader.ToDate, new Date())
                        .or(Criteria.isNull(QuestionnaireHeader.ToDate)));
        return getItems(query);
    }

    public List<QuestionnaireHeaderModel> getValidTemplatesForSupervisorCustomer(UUID customerId) {
        SupervisorCustomerManager supervisorCustomerManager = new SupervisorCustomerManager(getContext());
        SupervisorCustomerModel customer = supervisorCustomerManager.getItem(customerId);
        if (customer == null)
            throw new NullPointerException("CustomerId is not valid");
        String saleOfficeId = SysConfigManager.getValue(new SysConfigManager(getContext()).read(ConfigKey.SaleOfficeId, SysConfigManager.cloud));
        Query query = new Query();
        query.from(QuestionnaireHeader.QuestionnaireHeaderTbl)
                .whereAnd(Criteria.contains(QuestionnaireHeader.CenterUniqueIds, customer.CenterId == null ? UUID.randomUUID().toString() : customer.CenterId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CenterUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CenterUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CityUniqueIds, customer.CityId == null ? UUID.randomUUID().toString() : customer.CityId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CityUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CityUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CustomerActivityUniqueIds, customer.CustomerActivityId == null ? UUID.randomUUID().toString() : customer.CustomerActivityId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CustomerActivityUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CustomerActivityUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CustomerCategoryUniqueIds, customer.CustomerCategoryId == null ? UUID.randomUUID().toString() : customer.CustomerCategoryId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CustomerCategoryUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CustomerCategoryUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.CustomerLevelUniqueIds, customer.CustomerLevelId == null ? UUID.randomUUID().toString() : customer.CustomerLevelId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.CustomerLevelUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.CustomerLevelUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.SaleOfficeUniqueIds, saleOfficeId == null ? UUID.randomUUID().toString() : saleOfficeId)
                        .or(Criteria.isNull(QuestionnaireHeader.SaleOfficeUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.SaleOfficeUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.SaleZoneUniqueIds, customer.AreaId == null ? UUID.randomUUID().toString() : customer.AreaId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.SaleZoneUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.SaleZoneUniqueIds, "")))
                .whereAnd(Criteria.contains(QuestionnaireHeader.StateUniqueIds, customer.StateId == null ? UUID.randomUUID().toString() : customer.StateId.toString())
                        .or(Criteria.isNull(QuestionnaireHeader.StateUniqueIds))
                        .or(Criteria.equals(QuestionnaireHeader.StateUniqueIds, "")))
                .whereAnd(Criteria.lesserThan(QuestionnaireHeader.FromDate, new Date())
                        .or(Criteria.isNull(QuestionnaireHeader.FromDate)))
                .whereAnd(Criteria.greaterThan(QuestionnaireHeader.ToDate, new Date())
                        .or(Criteria.isNull(QuestionnaireHeader.ToDate)));
        return getItems(query);
    }
}
