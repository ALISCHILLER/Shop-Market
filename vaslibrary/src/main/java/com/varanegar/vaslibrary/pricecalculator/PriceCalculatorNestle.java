package com.varanegar.vaslibrary.pricecalculator;

import android.content.Context;

import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 12/19/2017.
 */

public class PriceCalculatorNestle extends PriceCalculator {
    public PriceCalculatorNestle(Context context, UUID customerId) {
        super(context, customerId);
    }

    public PriceCalculatorNestle(Context context, UUID customerId, UUID callOrderId, int payTypeId, int orderTypeId) {
        super(context, customerId, callOrderId, payTypeId, orderTypeId);
    }

    @Override
    public List<CustomerBatchPriceModel> calculateBatchPrices() {
        return null;
    }

    @Override
    public List<CustomerPriceModel> calculatePrices() {
        Date now = new Date();
        long sooner = now.getTime() - 100000;
        long later = now.getTime() + 100000;
        SysConfigModel sysConfigModel = new SysConfigManager(getContext()).read(ConfigKey.DcRef, SysConfigManager.cloud);
        Integer dcRef = SysConfigManager.getIntegerValue(sysConfigModel, null);
        String callOrderId = getCallOrderId() == null ? null : getCallOrderId().toString();
        String sql =
                "SELECT '" + getCustomerId().toString() + "' AS CustomerUniqueId, '" + callOrderId + "' AS CallOrderId, Product.UniqueId AS ProductUniqueId,ContractPriceNestle.UniqueId AS PriceId,ContractPriceNestle.ConditionAmount AS UserPrice,ContractPriceNestle.ConditionAmount AS Price\n" +
                        "FROM ContractPriceNestle INNER JOIN Product ON Product.BackOfficeId = ContractPriceNestle.MaterialNumber\n" +
                        "JOIN Customer C ON C.UniqueId = '" + getCustomerId().toString() + "' " +
                        "WHERE ConditionType = '" + dcRef  + "' AND ('" + now.getTime() + "' BETWEEN IFNULL(ContractPriceNestle.ConditionValidOn,'" + sooner + "') AND IFNULL(ContractPriceNestle.ConditionValidTo,'" + later + "'))\n" +
                        "And (CAST(IFNULL(IFNULL(ContractPriceNestle.CustomerHierarchyNumber,\tC.CustomerLevelRef),1)  as text) = CAST(IFNULL(C.CustomerLevelRef,1) as text)\n" +
                        "OR CAST(IFNULL(IFNULL(ContractPriceNestle.CustomerHierarchyNumber,\tC.CustomerActivityRef),1)  as text)=CAST(IFNULL(C.CustomerActivityRef,1) as text)\n" +
                        "OR CAST(IFNULL(IFNULL(ContractPriceNestle.CustomerHierarchyNumber,\tC.CustomerCategoryRef),1) as text)=CAST(IFNULL(C.CustomerCategoryRef,1)as text))\n";
        List<CustomerPriceModel> priceModels = new CustomerPriceModelRepository().getItems(sql, null);
        return priceModels;
    }
}
