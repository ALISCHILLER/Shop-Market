package varanegar.com.discountcalculatorlib.handler.vnlite;

import java.math.BigDecimal;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemStatutesVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempReturnItemSummaryFinalVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempReturnItemSummaryVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempReturnItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempSummaryFinalVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempSummaryVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;

public class PromotionFillEVCVnLiteV3 {

    public PromotionFillEVCVnLiteV3()
    {}

    public static String fillEVCV(DiscountCallOrderData callData, int evcTypeId)
    {
        String evcGuid = UUID.randomUUID().toString();
        clearOldEVCData(callData.customerId);
        fillEVCHeader(callData, evcGuid, evcTypeId);
        fillEVCItem(callData, evcGuid);
        //TODO these two methods has no body in Original too
        updateChargeTax(evcGuid);
        updateAmountNut(evcGuid);

        return evcGuid;
    }
    public static String fillEVCV(DiscountCallReturnData callData, EVCType evcTypeId) {

        String evcGuid1 = UUID.randomUUID().toString().toUpperCase();
        String evcGuid2 = UUID.randomUUID().toString().toUpperCase();

        clearOldEVCData(callData.customerId);
        clearOldReturnEVCData(callData.customerId);

        fillEVCHeader(callData, evcGuid1, evcGuid2, evcTypeId);
        fillEVCItem(callData, evcGuid1, evcGuid2);

        //fillCustomerTemp(evcGuid1, evcTypeId, callData.returnRefId);
        //fillTempTablesWithXML(callData, evcGuid1);
        //fillDiscountTemp(evcGuid1, evcTypeId, callData.returnRefId);

        return evcGuid1 + ":" + evcGuid2;
    }

    private static void clearOldReturnEVCData(int customerId)
    {
        String evcId = EVCHeaderVnLiteDBAdapter.getInstance().getEvcId(customerId);
        EVCTempReturnItemSummaryFinalVnLiteDBAdapter.getInstance().deleteAllEVCYTempReturnSummeryFinals();
        EVCTempReturnItemSummaryVnLiteDBAdapter.getInstance().deleteAllEVCYTempReturnSummeries();
        EVCTempReturnItemVnLiteDBAdapter.getInstance().deleteAllEVCYTempReturns();
    }


    /*
    private static void fillCustomerTemp(String evcId, GlobalBaseType.EVCType evcTypeId, int refId) {

        EVCTempCustomerVnLiteDBAdapter.getInstance().fillCustomerTemp(evcId, evcTypeId, refId);
    }

    private static void fillDiscountTemp(String evcId, GlobalBaseType.EVCType evcTypeId, int refId) {
        EVCTempDiscountVnLiteDBAdapter.getInstance().fillDiscountTemp(evcId, evcTypeId, refId);
    }
*/
    private static void fillEVCItem(DiscountCallReturnData callData, String evcId1, String evcId2) {

        EVCItemVnLiteDBAdapter.getInstance().saveEVCItem(callData, evcId1, evcId2);
    }


    private static void clearOldEVCData(int customerId)
    {
        EVCHeaderVnLiteDBAdapter.getInstance().clearAllData();
        EVCItemVnLiteDBAdapter.getInstance().clearAllData();
        EVCItemStatutesVnLiteDBAdapter.getInstance().clearAllData();
        clearTempEVCData();
    }

    private static void clearOldEVCDataOld(int customerId)
    {
        String evcId = EVCHeaderVnLiteDBAdapter.getInstance().getEvcId(customerId);
        if(evcId != null)
        {
            EVCHeaderVnLiteDBAdapter.getInstance().deleteEVCHeadersById(evcId);
            EVCItemVnLiteDBAdapter.getInstance().deleteAllEVCItemsById(evcId);
            EVCItemStatutesVnLiteDBAdapter.getInstance().deleteAllEVCItemStatutesById(evcId);
            EVCTempVnLiteDBAdapter.getInstance().deleteAllEVCTempsById(evcId);
            EVCTempSummaryVnLiteDBAdapter.getInstance().deleteAllEVCYTempSummarieById(evcId);
            EVCTempSummaryFinalVnLiteDBAdapter.getInstance().deleteAllEVCYTempSummarieById(evcId);
        }
    }
    private static void fillEVCHeader(DiscountCallOrderData callData, String evcId, int disTypeId)
    {
        EVCHeaderVnLiteDBAdapter.getInstance().saveEVCHeader(callData, evcId, disTypeId);
    }

    private static void fillEVCHeader(DiscountCallReturnData callReturnData, String evcId1, String evcId2, EVCType evcTypeId) {

        EVCHeaderVnLiteDBAdapter.getInstance().saveEVCHeader(callReturnData, evcId1, evcId2, evcTypeId);
    }


    private static void fillEVCItem(DiscountCallOrderData callData, String evcId)
    {
        for(DiscountCallOrderLineData lineData : callData.callOrderLineItemData)
        {
            if (lineData.invoiceTotalQty.compareTo(BigDecimal.ZERO) == 1 && !lineData.isRequestPrizeItem)
                EVCItemVnLiteDBAdapter.getInstance().saveEVCItem(lineData, evcId);
        }
    }

    private static void updateChargeTax(String uuId)
    {
    }

    private static void updateAmountNut(String uuId)
    {
    }

    public static void clearTempEVCData() {
        EVCTempVnLiteDBAdapter.getInstance().clearAllData();
        EVCTempSummaryVnLiteDBAdapter.getInstance().clearAllData();
        EVCTempSummaryFinalVnLiteDBAdapter.getInstance().clearAllData();


        EVCTempReturnItemVnLiteDBAdapter.getInstance().deleteAllEVCYTempReturns();
        EVCTempReturnItemSummaryVnLiteDBAdapter.getInstance().deleteAllEVCYTempReturnSummeries();
        EVCTempReturnItemSummaryFinalVnLiteDBAdapter.getInstance().deleteAllEVCYTempReturnSummeryFinals();

    }

}
