package com.varanegar.vaslibrary.pricecalculator;

import android.content.Context;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModel;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModelRepository;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 12/5/2017.
 */

class PriceCalculatorVnLite extends PriceCalculator {

    private int priceClassRef;

    public PriceCalculatorVnLite(Context context, UUID customerId) {
        super(context, customerId);
    }

    public PriceCalculatorVnLite(Context context, UUID customerId, UUID callOrderId, int payTypeId, int orderTypeId, int priceClassRef) {
        super(context, customerId, callOrderId, payTypeId, orderTypeId);
        this.priceClassRef = priceClassRef;
    }

    @Override
    public List<CustomerBatchPriceModel> calculateBatchPrices() {
        String callOrderId = getCallOrderId() == null ? null : "'" + getCallOrderId().toString() + "'";
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel publicPricePublic = sysConfigManager.read(ConfigKey.CustomerGeneralPrice, SysConfigManager.cloud);
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        List<CustomerBatchPriceModel> priceModels = null;
        if (SysConfigManager.compare(publicPricePublic, false)) {
            String sql = "SELECT case when ContractPriceVnLite.PriceClassRef != 0 then 4 when IFNULL(ContractPriceVnLite.CustomerRef, 0) != 0 then 3 when IFNULL(ContractPriceVnLite.CustomerGroupRef, 0) != 0 then 2 when IFNULL(ContractPriceVnLite.CustomerSubGroup1Ref, 0) != 0 OR IFNULL(ContractPriceVnLite.CustomerSubGroup2Ref, 0) != 0 then 1 else 0 end as Priority, Customer.UniqueId as CustomerUniqueId , Product.UniqueId as ProductUniqueId, ContractPriceVnLite.UniqueId as PriceId , ContractPriceVnLite.UserPrice as UserPrice, ContractPriceVnLite.BatchRef as BatchRef, ContractPriceVnLite.SellPrice as Price ," + callOrderId + " AS CallOrderId FROM Customer\n" +
                    "JOIN ContractPriceVnLite ON ContractPriceVnLite.CustomerRef = Customer.BackOfficeId OR ContractPriceVnLite.CustomerRef = 0\n" +
                    "JOIN Product ON ContractPriceVnLite.ProductRef = Product.BackOfficeId AND Product.IsActive = 1 AND (Product.IsForSale = 1 OR Product.IsForReturnWithRef = 1 OR Product.IsForReturnWithOutRef = 1)\n" +
                    "WHERE (IFNULL(ContractPriceVnLite.CustomerGroupRef ,Customer.CustomerCategoryRef ) = Customer.CustomerCategoryRef OR ContractPriceVnLite.CustomerGroupRef = 0)\n" +
                    "AND (IFNULL(ContractPriceVnLite.CustomerSubGroup1Ref ,Customer.CustomerSubGroup1Id ) = Customer.CustomerSubGroup1Id OR ContractPriceVnLite.CustomerSubGroup1Ref = 0)\n" +
                    "AND (IFNULL(ContractPriceVnLite.CustomerSubGroup2Ref ,Customer.CustomerSubGroup2Id ) = Customer.CustomerSubGroup2Id OR ContractPriceVnLite.CustomerSubGroup2Ref = 0)\n" +
                    "AND (IFNULL(ContractPriceVnLite.PriceClassRef , " + this.priceClassRef + ") = " + this.priceClassRef + " OR ContractPriceVnLite.PriceClassRef = 0)\n" +
                    "AND '" + date + "' BETWEEN ContractPriceVnLite.StartDate AND IFNULL(ContractPriceVnLite.EndDate, '" + date + "')\n" +
                    "AND Customer.UniqueId ='" + getCustomerId().toString() + "' \n" +
                    "AND ContractPriceVnLite.BatchRef <> 0 \n" +
                    "ORDER BY Priority DESC, ContractPriceVnLite.StartDate DESC, ContractPriceVnLite.BackOfficeNumberId DESC";
            priceModels = new CustomerBatchPriceModelRepository().getItems(sql, null);
        }
        return priceModels;
    }

    @Override
    public List<CustomerPriceModel> calculatePrices() {
        String callOrderId = getCallOrderId() == null ? null : "'" + getCallOrderId().toString() + "'";
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel publicPricePublic = sysConfigManager.read(ConfigKey.CustomerGeneralPrice, SysConfigManager.cloud);
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        List<CustomerPriceModel> priceModels = null;
        if (SysConfigManager.compare(publicPricePublic, false)) {
            String sql = "SELECT case when ContractPriceVnLite.PriceClassRef != 0 then 4 when IFNULL(ContractPriceVnLite.CustomerRef, 0) != 0 then 3 when IFNULL(ContractPriceVnLite.CustomerGroupRef, 0) != 0 then 2 when IFNULL(ContractPriceVnLite.CustomerSubGroup1Ref, 0) != 0 OR IFNULL(ContractPriceVnLite.CustomerSubGroup2Ref, 0) != 0 then 1 else 0 end as Priority, Customer.UniqueId as CustomerUniqueId , Product.UniqueId as ProductUniqueId, ContractPriceVnLite.UniqueId as PriceId , ContractPriceVnLite.UserPrice as UserPrice, ContractPriceVnLite.SellPrice as Price ," + callOrderId + " AS CallOrderId FROM Customer\n" +
                    "JOIN ContractPriceVnLite ON ContractPriceVnLite.CustomerRef = Customer.BackOfficeId OR ContractPriceVnLite.CustomerRef = 0\n" +
                    "JOIN Product ON ContractPriceVnLite.ProductRef = Product.BackOfficeId AND Product.IsActive = 1 AND (Product.IsForSale = 1 OR Product.IsForReturnWithRef = 1 OR Product.IsForReturnWithOutRef = 1)\n" +
                    "WHERE (IFNULL(ContractPriceVnLite.CustomerGroupRef ,Customer.CustomerCategoryRef ) = Customer.CustomerCategoryRef OR ContractPriceVnLite.CustomerGroupRef = 0)\n" +
                    "AND (IFNULL(ContractPriceVnLite.CustomerSubGroup1Ref ,Customer.CustomerSubGroup1Id ) = Customer.CustomerSubGroup1Id OR ContractPriceVnLite.CustomerSubGroup1Ref = 0)\n" +
                    "AND (IFNULL(ContractPriceVnLite.CustomerSubGroup2Ref ,Customer.CustomerSubGroup2Id ) = Customer.CustomerSubGroup2Id OR ContractPriceVnLite.CustomerSubGroup2Ref = 0)\n" +
                    "AND (IFNULL(ContractPriceVnLite.PriceClassRef , " + this.priceClassRef + ") = " + this.priceClassRef + " OR ContractPriceVnLite.PriceClassRef = 0)\n" +
                    "AND '" + date + "' BETWEEN ContractPriceVnLite.StartDate AND IFNULL(ContractPriceVnLite.EndDate, '" + date + "')\n" +
                    "AND Customer.UniqueId ='" + getCustomerId().toString() + "' \n" +
                    "AND ContractPriceVnLite.BatchRef = 0 \n" +
                    "ORDER BY Priority DESC, ContractPriceVnLite.StartDate DESC, ContractPriceVnLite.BackOfficeNumberId DESC";
            priceModels = new CustomerPriceModelRepository().getItems(sql, null);
        } else {
            String sql = "SELECT case when ContractPriceVnLite.PriceClassRef != 0 then 4 when IFNULL(ContractPriceVnLite.CustomerRef, 0) != 0 then 3 when IFNULL(ContractPriceVnLite.CustomerGroupRef, 0) != 0 then 2 when IFNULL(ContractPriceVnLite.CustomerSubGroup1Ref, 0) != 0 OR IFNULL(ContractPriceVnLite.CustomerSubGroup2Ref, 0) != 0 then 1 else 0 end as Priority, '" + getCustomerId().toString() + "' as CustomerUniqueId , Product.UniqueId as ProductUniqueId, ContractPriceVnLite.UniqueId as PriceId , ContractPriceVnLite.UserPrice as UserPrice, ContractPriceVnLite.SellPrice as Price, " + callOrderId + " AS CallOrderId\n" +
                    "FROM ContractPriceVnLite\n" +
                    "JOIN Product ON ContractPriceVnLite.ProductRef = Product.BackOfficeId AND Product.IsActive = 1 AND (Product.IsForSale = 1 OR Product.IsForReturnWithRef = 1 OR Product.IsForReturnWithOutRef = 1)\n" +
                    "WHERE IFNULL(ContractPriceVnLite.CustomerGroupRef,0)= 0\n" +
                    "AND IFNULL(ContractPriceVnLite.CustomerSubGroup1Ref,0) = 0\n" +
                    "AND IFNULL(ContractPriceVnLite.CustomerSubGroup2Ref,0) = 0\n" +
                    "AND IFNULL(ContractPriceVnLite.PriceClassRef,0) = 0\n" +
                    "AND '" + date + "' BETWEEN ContractPriceVnLite.StartDate AND IFNULL(ContractPriceVnLite.EndDate, '" + date + "')\n" +
                    "AND IFNULL(ContractPriceVnLite.CustomerRef,0) = 0\n" +
                    "AND ContractPriceVnLite.BatchRef = 0 \n" +
                    "ORDER BY Priority DESC, ContractPriceVnLite.StartDate DESC, ContractPriceVnLite.BackOfficeNumberId DESC";
            priceModels = new CustomerPriceModelRepository().getItems(sql, null);
        }
        return priceModels;
    }
}
