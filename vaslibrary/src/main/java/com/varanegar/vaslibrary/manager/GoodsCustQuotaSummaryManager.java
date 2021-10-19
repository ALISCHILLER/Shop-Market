package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.productUnit.ProductUnitManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.goodscusttemp.GoodsCustTempModel;
import com.varanegar.vaslibrary.model.productUnit.ProductUnitModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.order.GoodsCustQuotaSummaryModel;
import com.varanegar.vaslibrary.ui.fragment.order.GoodsCustQuotaSummaryModelRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 11/19/2018.
 */

public class GoodsCustQuotaSummaryManager extends BaseManager<GoodsCustQuotaSummaryModel> {
    public GoodsCustQuotaSummaryManager(@NonNull Context context) {
        super(context, new GoodsCustQuotaSummaryModelRepository());
    }

    public List<GoodsCustQuotaSummaryModel> getTempTableData(CustomerModel customer) {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        String date = simpleDateFormat.format(now);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SaleOfficeRef, SysConfigManager.cloud);
        String query = "  SELECT DISTINCT D.ID AS GoodsCustID," +
                "  D.StartDate AS StartDate," +
                "  D.EndDate AS EndDate," +
                "  OI.ProductId AS ItemRef," +
                "  OI.UniqueId as RowOrder," +
                "  D.RuleNo AS RuleNo," +
                "  D.GoodsRef AS GoodsRef," +
                "  D.GoodsGroupRef AS GoodsGroupRef," +
                "  D.GoodsCtgrRef AS GoodsCtgrRef," +
                "  D.MainTypeRef AS MainTypeRef," +
                "  D.SubTypeRef AS SubTypeRef," +
                "  D.DcRef AS DcRef," +
                "  D.CustRef AS CustRef," +
                "  D.CustCtgrRef AS CustCtgrRef," +
                "  D.CustActRef AS CustActRef," +
                "  D.StateRef AS StateRef," +
                "  D.CountyRef AS CountyRef," +
                "  D.AreaRef AS AreaRef," +
                "  D.SaleOfficeRef AS SaleOfficeRef," +
                "  D.UnitUniqueId AS UnitUniqueId," +
                "  D.ManufacturerRef AS ManufacturerRef," +
                "  D.CustLevelRef AS CustLevelRef," +
                "  oi.Qty AS ReqQty," +
                "  D.MINQty AS MINQty," +
                "  D.MAXQty AS MAXQty," +
//                "  (" +
//                "  CASE " +
//                "   WHEN (D.GoodsRef IS NOT NULL) THEN IFNULL(P1.Quantity, 1)" +
//                "   WHEN ((D.GoodsRef IS NULL) AND D.UnitRef <> 0) THEN  1" +
//                "   WHEN D.ApplyInGroup = 0 THEN IFNULL(P2.Quantity, 1)" +
//                "   ELSE 1" +
//                "  END" +
//                "  ) AS UnitCapasity," +
                "  D.ApplyInGroup as ApplyInGroup," +
                "  Date('now') as OrderDate," +
                "  g.ProductCode as ProductCode," +
                "  g.UniqueId as ProductUniqueId," +
                "  g.ProductName as ProductName " +
//                "  g.LargeUnitQty as LargeUnitQty" +
                " FROM   GoodsCustQuotas D" +
                "  LEFT OUTER JOIN GoodsCustTemp oi ON  D.goodUniqueId = oi.ProductId" +
//                "  LEFT OUTER JOIN ProductUnit P1 ON  D.GoodsRef = P1.ProductId AND D.UnitUniqueId = P1.UnitId" +
//                "  LEFT OUTER JOIN ProductUnit P2 ON  oi.ProductId = P2.ProductId AND oi.UnitUniqueId = P2.UnitId " +
                "  LEFT OUTER JOIN product g ON  g.UniqueId = oi.ProductId" +
                //"  LEFT OUTER JOIN ProductMainSubType GM  ON  D.GoodsRef = GM.GoodsRef" +
                " WHERE (D.AreaRef is null or D.AreaRef = 0 or D.AreaRef = IFNULL(" + customer.SaleAreaRef + ", 0))" +
                "  AND (D.CountyRef is null or D.CountyRef = 0 or D.CountyRef = IFNULL(" + customer.CountyRef + ", 0))" +
                "  AND (D.StateRef is null or D.StateRef = 0 or StateRef = IFNULL(" + customer.StateRef + ", 0))" +
                "  AND (D.DCRef is null or D.DCRef = 0 or D.DCRef = " + customer.DCRef +  ")" +
                "  AND (D.CustActRef is null or D.CustActRef = 0 or D.CustActRef = IFNULL(" + customer.CustomerActivityRef + ", 0))" +
                "  AND (D.CustCtgrRef is null or D.CustCtgrRef = 0 or D.CustCtgrRef = IFNULL("  + customer.CustomerCategoryRef +  ", 0))" +
                "  AND (D.CustLevelRef is null or D.CustLevelRef = 0 or D.CustLevelRef = IFNULL(" + customer.CustomerLevelRef + ", 0))" +
                "  AND (D.CustRef is null or D.CustRef = 0 or D.CustRef = " + customer.BackOfficeId + ")" +
                "  AND (D.SaleOfficeRef is null or D.SaleOfficeRef = 0 or D.SaleOfficeRef = " + sysConfigModel.Value + ")" +
                "  AND (" +
                "  D.GoodsGroupRef IS NULL OR D.GoodsGroupRef = 0 " +
                "  OR g.ProductBoGroupId IN (SELECT C.UniqueId " +
                " FROM   ProductBoGroup C" +
                " INNER JOIN ProductBoGroup" +
                " P" +
                " ON  C.NLEFT " +
                " BETWEEN P.NLEFT " +
                " AND P.NRIGHT" +
                " WHERE  P.UniqueId = D.goodGroupUniqueId)" +
                "  )" +
                //"  AND IFNULL(D.MainTypeRef, IFNULL(GM.MainTypeRef, 0)) = IFNULL(GM.MainTypeRef, 0)" +
                //"  AND IFNULL(D.SubTypeRef, IFNULL(GM.SubTypeRef, 0)) = IFNULL(GM.SubTypeRef, 0)" +
                " AND D.GoodsCtgrRef = 0 or IFNULL(" +
                " D.GoodsCtgrRef," +
                " IFNULL(G.ProductCtgrRef, 0) "+
                " ) = IFNULL(G.ProductCtgrRef, 0) " +
                "  AND IFNULL(D.ManufacturerRef, g.ManufacturerId) = g.ManufacturerId" +
                "  AND IFNULL(D.goodUniqueId, oi.ProductId) = oi.ProductId" +
                "  AND ((D.EndDate IS NULL AND D.StartDate<='" + date + "') Or ('" + date + "' BETWEEN D.StartDate AND D.EndDate))\n" +
                //"  AND date('now') BETWEEN IFNULL(D.StartDate, date('now')) AND IFNULL(D.EndDate, date('now'))" +
                " ";

        List<GoodsCustQuotaSummaryModel> goodsCustQuotaSummaryModels = new GoodsCustQuotaSummaryModelRepository().getItems(query, null);

//        if (result != null && result.size() > 0) {
//            for (GoodsCustQuotaSummaryModel goodsCustQuotaSummaryModel : goodsCustQuotaSummaryModels) {
//                String unitId = goodsCustQuotaSummaryModel.UnitUniqueId;
//                int minQty = goodsCustQuotaSummaryModel.MINQty;
//                int maxQty = goodsCustQuotaSummaryModel.MAXQty;
//                if (unitId.equals("0")) {
//                    int qty = goodsCustQuotaSummaryModel.LargeUnitQty;
//                    minQty = minQty * qty;
//                    maxQty = maxQty * qty;
//                    goodsCustQuotaSummaryModel.MINQty = minQty;
//                    goodsCustQuotaSummaryModel.MAXQty = maxQty;
//                }
//                result.add(goodsCustQuotaSummaryModel);
//            }
//        }

        return goodsCustQuotaSummaryModels;

    }

    public String checkCustomerProductLimitation(Context context, List<CustomerCallOrderOrderViewModel> items, CustomerModel customer) {
        String errorMessage = "";
        List<CustomerCallOrderOrderViewModel> customerCallOrderOrderViewModels = items;
        if (customerCallOrderOrderViewModels != null && customerCallOrderOrderViewModels.size() > 0) {
            if (saveOrderTempData(customerCallOrderOrderViewModels)) {
                GoodsCustQuotaSummaryManager goodsCustQuotaSummaryManager = new GoodsCustQuotaSummaryManager(getContext());
                List<GoodsCustQuotaSummaryModel> limitInfo = goodsCustQuotaSummaryManager.getTempTableData(customer);
                HashMap<String, GoodsCustQuotaSummaryModel> orderSummary = getOrderSummary(limitInfo, context);
                List<LimitationResult> result = calcLimitationResult(customerCallOrderOrderViewModels, limitInfo, orderSummary);
                for(LimitationResult resultData : result)
                {
                    errorMessage += context.getString(R.string.because_of_the_law_number) + " " + resultData.RuleNo + context.getString(R.string.not_allowed_sale_product)  + " " + resultData.GoodsCode + " " + context.getString(R.string.from) + " " + resultData.MINQty + " " + context.getString(R.string.to) + " " + resultData.MAXQty + " " + context.getString(R.string.is) + "\r\n";
                }
            }
        }
        return errorMessage;
    }

    private boolean saveOrderTempData(List<CustomerCallOrderOrderViewModel> customerCallOrderOrderViewModels) {
        GoodsCustTempManager custTempManager = new GoodsCustTempManager(getContext());
        try {
            custTempManager.deleteAll();
            for (CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel : customerCallOrderOrderViewModels) {
                if (customerCallOrderOrderViewModel.TotalQty.compareTo(BigDecimal.ZERO) > 0) {
                    GoodsCustTempModel goodsCustTempModel = new GoodsCustTempModel();
                    goodsCustTempModel.UniqueId = customerCallOrderOrderViewModel.UniqueId;
                    goodsCustTempModel.Qty = customerCallOrderOrderViewModel.TotalQty.intValue();
                    goodsCustTempModel.ProductId = customerCallOrderOrderViewModel.ProductId;
                    goodsCustTempModel.UnitId = customerCallOrderOrderViewModel.ProductUnitId;
                    try {
                        custTempManager.insert(goodsCustTempModel);
                    } catch (ValidationException e) {
                        e.printStackTrace();
                        Timber.e(e);
                        return false;
                    }
                }
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            Timber.e(e);
            return false;
        }
    }

    private static HashMap<String, GoodsCustQuotaSummaryModel> getOrderSummary(List<GoodsCustQuotaSummaryModel> limitInfo, Context context) {
        HashMap<String, GoodsCustQuotaSummaryModel> result = new HashMap<>();
        HashMap<String, GoodsCustQuotaSummaryModel> tempHashData = new HashMap<>();
        for (GoodsCustQuotaSummaryModel tempLimitData : limitInfo) {
            ProductUnitModel productUnitModel = (new ProductUnitManager(context)).getItem(tempLimitData.ProductUniqueId, tempLimitData.UnitUniqueId);
            tempLimitData.MAXQty = tempLimitData.MAXQty * (productUnitModel == null ? 0 : productUnitModel.ConvertFactor.intValue());
            tempLimitData.MINQty = tempLimitData.MINQty * (productUnitModel == null ? 0 : productUnitModel.ConvertFactor.intValue());
            if (tempLimitData.ApplyInGroup == 0 && (tempLimitData.ReqQty < tempLimitData.MINQty || tempLimitData.ReqQty > tempLimitData.MAXQty)) {
                GoodsCustQuotaSummaryModel tempData = new GoodsCustQuotaSummaryModel();
                tempData.GoodsCustID = tempLimitData.GoodsCustID;
                tempData.ItemRef = tempLimitData.ItemRef;
                tempData.RowOrder = tempLimitData.RowOrder;
                tempData.ReqQty = tempLimitData.ReqQty;
                tempData.RuleNo = tempLimitData.RuleNo;
                tempData.MINQty = tempLimitData.MINQty;
                tempData.MAXQty = tempLimitData.MAXQty;
                result.put(tempData.ItemRef, tempData);
            }

            if (tempLimitData.ApplyInGroup == 1) {
                GoodsCustQuotaSummaryModel tempData = new GoodsCustQuotaSummaryModel();
                if (tempHashData.containsKey(Integer.toString(tempLimitData.GoodsCustID))) {
                    tempData = tempHashData.get(Integer.toString(tempLimitData.GoodsCustID));
                    tempData.ReqQty += tempLimitData.ReqQty;
                    if (tempData.MAXQty < tempLimitData.MAXQty)
                        tempData.MAXQty = tempLimitData.MAXQty;
                    if (tempData.MINQty < tempLimitData.MINQty)
                        tempData.MINQty = tempLimitData.MINQty;
                    tempHashData.put(Integer.toString(tempLimitData.GoodsCustID), tempData);
                } else {
                    tempData.GoodsCustID = tempLimitData.GoodsCustID;
                    tempData.ItemRef = tempLimitData.ItemRef;
                    tempData.RowOrder = tempLimitData.RowOrder;
                    tempData.ReqQty = tempLimitData.ReqQty;
                    tempData.MINQty = tempLimitData.MINQty;
                    tempData.MAXQty = tempLimitData.MAXQty;
                    tempData.RuleNo = tempLimitData.RuleNo;
                    tempHashData.put(Integer.toString(tempLimitData.GoodsCustID), tempData);
                }
            }
        }

        for (GoodsCustQuotaSummaryModel tempLimitData : limitInfo) {
            if (tempHashData.containsKey(Integer.toString(tempLimitData.GoodsCustID))) {
                GoodsCustQuotaSummaryModel hashData = tempHashData.get(Integer.toString(tempLimitData.GoodsCustID));
                if (hashData.ReqQty < hashData.MINQty || hashData.ReqQty > hashData.MAXQty) {
                    GoodsCustQuotaSummaryModel tempData = new GoodsCustQuotaSummaryModel();
                    tempData.GoodsCustID = tempLimitData.GoodsCustID;
                    tempData.ItemRef = tempLimitData.ItemRef;
                    tempData.RowOrder = tempLimitData.RowOrder;
                    tempData.ReqQty = tempLimitData.ReqQty;
                    tempData.MINQty = tempLimitData.MINQty;
                    tempData.MAXQty = tempLimitData.MAXQty;
                    tempData.RuleNo = tempLimitData.RuleNo;

                    result.put(tempData.ItemRef, tempData);
                }
            }
        }
        return result;
    }

    private static List<LimitationResult> calcLimitationResult(List<CustomerCallOrderOrderViewModel> customerCallOrderOrderViewModels, List<GoodsCustQuotaSummaryModel> limitInfo, HashMap<String, GoodsCustQuotaSummaryModel> orderSummary) {
        List<LimitationResult> result = new ArrayList<>();
        for (CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel : customerCallOrderOrderViewModels) {
            if (customerCallOrderOrderViewModel.TotalQty.compareTo(BigDecimal.ZERO) > 0) {
                if (orderSummary.containsKey(customerCallOrderOrderViewModel.ProductId.toString())) {
                    LimitationResult resultData = new LimitationResult();
                    resultData.GoodsCode = customerCallOrderOrderViewModel.ProductCode;
                    resultData.GoodsName = customerCallOrderOrderViewModel.ProductName;
                    resultData.MINQty = orderSummary.get((customerCallOrderOrderViewModel.ProductId.toString())).MINQty;
                    resultData.MAXQty = orderSummary.get((customerCallOrderOrderViewModel.ProductId.toString())).MAXQty;
                    resultData.RuleNo = orderSummary.get((customerCallOrderOrderViewModel.ProductId.toString())).RuleNo;
                    result.add(resultData);
                }
            }
        }
        return result;

    }


}

class LimitationResult {
    public String GoodsCode;
    public String GoodsName;
    public int RuleNo;
    public int MINQty;
    public int MAXQty;
}
