package com.varanegar.vaslibrary.manager.picture;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateHeader;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateHeaderModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateHeaderModelRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/18/2017.
 */

public class PictureTemplateHeaderManager extends BaseManager<PictureTemplateHeaderModel> {
    public PictureTemplateHeaderManager(@NonNull Context context) {
        super(context, new PictureTemplateHeaderModelRepository());
    }

    @NonNull
    public List<PictureTemplateHeaderModel> getValidTemplatesForCustomer(@NonNull UUID customerId) {
        CustomerManager customerManager = new CustomerManager(getContext());
        CustomerModel customer = customerManager.getItem(customerId);
        if (customer == null)
            throw new NullPointerException("CustomerId is not valid");
        String saleOfficeId = SysConfigManager.getValue(new SysConfigManager(getContext()).read(ConfigKey.SaleOfficeId, SysConfigManager.cloud));
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        Query query = new Query();
        query.from(PictureTemplateHeader.PictureTemplateHeaderTbl)
                .whereAnd(Criteria.contains(PictureTemplateHeader.CenterUniqueIds, customer.CenterId == null ? UUID.randomUUID().toString() : customer.CenterId.toString())
                        .or(Criteria.isNull(PictureTemplateHeader.CenterUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.CenterUniqueIds, "")))
                .whereAnd(Criteria.contains(PictureTemplateHeader.CityUniqueIds, customer.CityId == null ? UUID.randomUUID().toString() : customer.CityId.toString())
                        .or(Criteria.isNull(PictureTemplateHeader.CityUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.CityUniqueIds, "")))
                .whereAnd(Criteria.contains(PictureTemplateHeader.CustomerActivityUniqueIds, customer.CustomerActivityId == null ? UUID.randomUUID().toString() : customer.CustomerActivityId.toString())
                        .or(Criteria.isNull(PictureTemplateHeader.CustomerActivityUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.CustomerActivityUniqueIds, "")))
                .whereAnd(Criteria.contains(PictureTemplateHeader.CustomerCategoryUniqueIds, customer.CustomerCategoryId == null ? UUID.randomUUID().toString() : customer.CustomerCategoryId.toString())
                        .or(Criteria.isNull(PictureTemplateHeader.CustomerCategoryUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.CustomerCategoryUniqueIds, "")))
                .whereAnd(Criteria.contains(PictureTemplateHeader.CustomerLevelUniqueIds, customer.CustomerLevelId == null ? UUID.randomUUID().toString() : customer.CustomerLevelId.toString())
                        .or(Criteria.isNull(PictureTemplateHeader.CustomerLevelUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.CustomerLevelUniqueIds, "")))
                .whereAnd(Criteria.contains(PictureTemplateHeader.SaleOfficeUniqueIds, saleOfficeId == null ? UUID.randomUUID().toString() : saleOfficeId)
                        .or(Criteria.isNull(PictureTemplateHeader.SaleOfficeUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.SaleOfficeUniqueIds, "")))
                .whereAnd(Criteria.contains(PictureTemplateHeader.SaleZoneUniqueIds, customer.AreaId == null ? UUID.randomUUID().toString() : customer.AreaId.toString())
                        .or(Criteria.isNull(PictureTemplateHeader.SaleZoneUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.SaleZoneUniqueIds, "")))
                .whereAnd(Criteria.contains(PictureTemplateHeader.StateUniqueIds, customer.StateId == null ? UUID.randomUUID().toString() : customer.StateId.toString())
                        .or(Criteria.isNull(PictureTemplateHeader.StateUniqueIds))
                        .or(Criteria.equals(PictureTemplateHeader.StateUniqueIds, "")))
                .whereAnd(Criteria.lesserThanOrEqual(PictureTemplateHeader.FromPDate, date)
                        .or(Criteria.isNull(PictureTemplateHeader.FromPDate)))
                .whereAnd(Criteria.greaterThanOrEqual(PictureTemplateHeader.ToPDate, date)
                        .or(Criteria.isNull(PictureTemplateHeader.ToPDate)));
        return getItems(query);
    }
}
