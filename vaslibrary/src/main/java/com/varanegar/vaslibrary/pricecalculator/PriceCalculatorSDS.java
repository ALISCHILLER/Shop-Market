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
 * Created by A.Jafarzadeh on 6/11/2017.
 */

public class PriceCalculatorSDS extends PriceCalculator {

    public PriceCalculatorSDS(Context context, UUID customerId, UUID callOrderId, int payTypeId, int orderTypeId) {
        super(context, customerId, callOrderId, payTypeId, orderTypeId);
    }

    public PriceCalculatorSDS(Context context, UUID customerId) {
        super(context, customerId);
    }

    @Override
    public List<CustomerBatchPriceModel> calculateBatchPrices() {
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        // Warning We replace all UniqueIds after query finished!
        SysConfigModel sysConfigModel = new SysConfigManager(getContext()).read(ConfigKey.DcRef, SysConfigManager.cloud);
        Integer dcRef = SysConfigManager.getIntegerValue(sysConfigModel, null);
        String sql = "SELECT ProductBatchOnHandQty.BatchRef AS BatchRef, CP.UniqueId, C.UniqueId AS CustomerUniqueId, CP.SalePrice AS Price, P.UniqueId AS ProductUniqueId, PR.UserPrice AS UserPrice, CP.UniqueId AS PriceId\n" +
                "FROM ContractPriceSDS CP\n" +
                "JOIN Customer C ON C.UniqueId = '" + getCustomerId().toString() + "' " +
                "JOIN (SELECT * FROM Product where (IsForSale = 1 or IsFreeItem = 1 or IsForReturnWithRef = 1 or IsForReturnWithOutRef = 1)) AS  p ON ((" +
                "CP.GoodsRef IS NOT NULL\n" +
                "AND CP.GoodsRef = P.BackOfficeId)\n" +
                "OR (CP.GoodsRef IS NULL\n" +
                "AND (CP.GoodsGroupRef IS NULL OR CP.GoodsGroupRef IN (SELECT GGP.UniqueId FROM ProductBoGroup GGC INNER JOIN ProductBoGroup GGP ON GGC.NLeft BETWEEN GGP.NLeft AND GGP.NRight WHERE GGC.UniqueId = P.ProductBoGroupId))\n" +
                "AND (CP.MainTypeRef IS NULL OR CP.MainTypeRef IN ( SELECT MainTypeRef FROM ProductMainSubType WHERE GoodsRef = P.BackOfficeId ))\n" +
                "AND (CP.SubTypeRef IS NULL OR CP.SubTypeRef IN (SELECT SubTypeRef FROM ProductMainSubType WHERE GoodsRef = P.BackOfficeId))))\n" +

                "JOIN  ProductBatchOnHandQty ON ProductBatchOnHandQty.ProductId = p.UniqueId AND ProductBatchOnHandQty.BatchRef = CP.BatchNoRef\n " +
                "JOIN PriceHistory PR ON PR.GoodsRef = P.BackOfficeId AND " +
                " pr.UniqueId = ( SELECT tP2.UniqueId FROM  PriceHistory tP2 WHERE tP2.GoodsRef = P.BackOfficeId AND ('" + date + "' BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, '" + date + "')) AND tp2.IsActive = 1 AND ( tp2.DCRef IS NULL OR tp2.DCRef = 0 OR IFNULL(tp2.DCRef, " + dcRef + ") = C.DCRef) " +
                "ORDER BY tP2.DCRef DESC LIMIT 1) " +
                "WHERE 1 = 1\n" +
                "AND ((CP.EndDate IS NULL AND CP.StartDate <= '" + date + "' ) OR ( '" + date + "' BETWEEN CP.StartDate AND CP.EndDate))\n" +
                "AND IFNULL(IFNULL(CP.CustRef, C.BackOfficeId),1) = IFNULL(C.BackOfficeId,1)\n" +
                "AND IFNULL(IFNULL(CP.CustCtgrRef,\tC.CustomerCategoryRef),1) = IFNULL(C.CustomerCategoryRef,1)\n" +
                "AND IFNULL(IFNULL(CP.CustActRef,\tC.CustomerActivityRef),1) = IFNULL(C.CustomerActivityRef,1)\n" +
                "AND IFNULL(IFNULL(CP.StateRef, C.StateRef),1) = IFNULL(C.StateRef,1)\n" +
                "AND IFNULL(IFNULL(CP.CountyRef, C.CountyRef),1) = IFNULL(C.CountyRef,1)\n" +
                "AND IFNULL(IFNULL(CP.AreaRef, C.CityRef),1) = IFNULL(C.CityRef,1)\n" +
                "AND (IFNULL(CP.CustLevelRef, 0) = 0 OR IFNULL(C.CustomerLevelRef, 0) = IFNULL(CP.CustLevelRef, 0))\n" +

                "AND IFNULL(IFNULL(CP.DcRef," + dcRef + "),1) = IFNULL(" + dcRef + ",1)\n" +
                "AND IFNULL(BuyTypeRef,  " + payTypeId + ") =  " + payTypeId + " --  AND IFNULL(CP.UsanceDay, 0)=0 \n" +
                "AND IFNULL(OrderTypeRef, " + orderTypeId + ") = " + orderTypeId + " " +
                "AND CP.Priority = 1\n" +
                "ORDER BY CP.GoodsRef DESC, CP.Priority";

        List<CustomerBatchPriceModel> customerBatchPriceModels = new CustomerBatchPriceModelRepository().getItems(sql, null);
        return customerBatchPriceModels;
    }

    @Override
    public List<CustomerPriceModel> calculatePrices() {
        String callOrderId = getCallOrderId() == null ? null : "'" + getCallOrderId().toString() + "'";
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel publicPricePublic = sysConfigManager.read(ConfigKey.CustomerGeneralPrice, SysConfigManager.cloud);
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        List<CustomerPriceModel> priceModels;
        SysConfigModel sysConfigModel = new SysConfigManager(getContext()).read(ConfigKey.DcRef, SysConfigManager.cloud);
        Integer dcRef = SysConfigManager.getIntegerValue(sysConfigModel, null);
        if (SysConfigManager.compare(publicPricePublic, false)) {
            // Warning We replace all UniqueIds after query finished!
            String sql = "SELECT CP.UniqueId, C.UniqueId AS CustomerUniqueId, CP.SalePrice AS Price, P.UniqueId AS ProductUniqueId, PR.UserPrice AS UserPrice, CP.UniqueId AS PriceId, " + callOrderId + " AS CallOrderId\n" +
                    "FROM ContractPriceSDS CP\n" +
                    "JOIN Customer C ON C.UniqueId = '" + getCustomerId().toString() + "' " +
                    "JOIN (SELECT * FROM Product where (IsForSale = 1 or IsFreeItem = 1 or IsForReturnWithRef = 1 or IsForReturnWithOutRef = 1)) AS  p ON ((" +
                    "CP.GoodsRef IS NOT NULL\n" +
                    "AND CP.GoodsRef = P.BackOfficeId))\n" +
                    "JOIN PriceHistory PR ON PR.GoodsRef = P.BackOfficeId AND " +
                    " pr.UniqueId = ( SELECT tP2.UniqueId FROM  PriceHistory tP2 WHERE tP2.GoodsRef = P.BackOfficeId AND ('" + date + "' BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, '" + date + "')) AND tp2.IsActive = 1 AND ( tp2.DCRef IS NULL OR tp2.DCRef = 0 OR IFNULL(tp2.DCRef, " + dcRef + ") = C.DCRef) " +
                    "ORDER BY tP2.DCRef DESC LIMIT 1) " +
                    "WHERE 1 = 1\n" +
                    "AND ((CP.EndDate IS NULL AND CP.StartDate <= '" + date + "' ) OR ( '" + date + "' BETWEEN CP.StartDate AND CP.EndDate))\n" +
                    "AND IFNULL(IFNULL(CP.CustRef, C.BackOfficeId),1) = IFNULL(C.BackOfficeId,1)\n" +
                    "AND IFNULL(IFNULL(CP.CustCtgrRef,\tC.CustomerCategoryRef),1) = IFNULL(C.CustomerCategoryRef,1)\n" +
                    "AND IFNULL(IFNULL(CP.CustActRef,\tC.CustomerActivityRef),1) = IFNULL(C.CustomerActivityRef,1)\n" +
                    "AND IFNULL(IFNULL(CP.StateRef, C.StateRef),1) = IFNULL(C.StateRef,1)\n" +
                    "AND IFNULL(IFNULL(CP.CountyRef, C.CountyRef),1) = IFNULL(C.CountyRef,1)\n" +
                    "AND IFNULL(IFNULL(CP.AreaRef, C.CityRef),1) = IFNULL(C.CityRef,1)\n" +
                    "AND (IFNULL(CP.CustLevelRef, 0) = 0 OR IFNULL(C.CustomerLevelRef, 0) = IFNULL(CP.CustLevelRef, 0))\n" +
                    "AND IFNULL(IFNULL(CP.DcRef," + dcRef + "),1) = IFNULL(" + dcRef + ",1)\n" +
                    "AND IFNULL(BuyTypeRef,  " + payTypeId + ") =  " + payTypeId + " --  AND IFNULL(CP.UsanceDay, 0)=0 \n" +
                    "AND IFNULL(OrderTypeRef, " + orderTypeId + ") = " + orderTypeId + " " +
                    "AND CP.Priority <> 1\n" +
                    "ORDER BY CP.GoodsRef DESC, CP.Priority";

            priceModels = new CustomerPriceModelRepository().getItems(sql, null);
        } else {
            String sql = "SELECT CP.UniqueId, '" + getCustomerId().toString() + "' AS CustomerUniqueId, " + callOrderId + " AS CallOrderId,\n" +
                    "CP.SalePrice AS Price,\n" +
                    "        P.UniqueId AS ProductUniqueId,\n" +
                    "PR.UserPrice AS UserPrice, \n" +
                    "        CP.UniqueId AS PriceId\n" +
                    "FROM ContractPriceSDS cp\n" +
                    "JOIN Product p ON cp.GoodsRef = p.BackOfficeId AND (p.IsForSale = 1 or IsFreeItem = 1 or p.IsForReturnWithRef = 1 or p.IsForReturnWithOutRef = 1)\n" +
                    "JOIN PriceHistory PR ON PR.GoodsRef = P.BackOfficeId AND\n" +
                    "pr.UniqueId = ( SELECT tP2.UniqueId FROM  PriceHistory tP2 WHERE tP2.GoodsRef = P.BackOfficeId AND ('" + date + "' BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, '" + date + "')) AND tp2.IsActive = 1 AND ( tp2.DCRef IS NULL OR tp2.DCRef = 0 OR IFNULL(tp2.DCRef," + dcRef + ") = " + dcRef + "))\n" +
                    "JOIN (\n" +
                    "        SELECT CP.GoodsRef , Max(SalePrice) MSalePrice\n" +
                    "        FROM 	ContractPriceSDS cp\n" +
                    "        WHERE	cp.CustActRef IS NULL\n" +
                    "        AND cp.CustCtgrRef IS NULL\n" +
                    "        AND cp.CustLevelRef IS NULL\n" +
                    "        AND cp.StateRef IS NULL\n" +
                    "        AND cp.CountyRef IS NULL\n" +
                    "        AND cp.AreaRef IS NULL\n" +
                    "        AND cp.CustRef IS NULL\n" +
                    "        AND (IFNULL(IFNULL(CP.DcRef," + dcRef + "),1) = IFNULL(" + dcRef + ",1) OR " + dcRef + "=0)\n" +
                    "        AND ((CP.EndDate IS NULL AND CP.StartDate <= '" + date + "' ) OR ( '" + date + "' BETWEEN CP.StartDate AND CP.EndDate))\n" +
                    "GROUP BY CP.GoodsRef ) as cp2\n" +
                    "WHERE cp2.GoodsRef = cp.GoodsRef\n" +
                    "AND MSalePrice = cp.SalePrice\n";
            priceModels = new CustomerPriceModelRepository().getItems(sql, null);
        }
        return priceModels;
    }
}
