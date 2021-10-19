package varanegar.com.discountcalculatorlib.handler.sds;

import java.math.BigDecimal;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import  varanegar.com.discountcalculatorlib.dataadapter.evc.sds.*;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountOrderPrizeDBAdapter;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeType;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.utility.enumerations.ReturnType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;

public class PromotionFillEVCSDSV3 {

    private int roundType = 0;

    public PromotionFillEVCSDSV3() {

    }


    public static String fillEVC(DiscountCallOrderData callData, EVCType evcType) {
        Timber.d("calcPromotionSDS : fillEVC started ");

        String evcGuid = UUID.randomUUID().toString();

        clearOldEVCData();

        fillEVCHeader(callData, evcGuid, evcType.value(), callData.disTypeId);
        fillEVCItem(callData, evcGuid);
        fillCustomerTemp(evcGuid, evcType, 0);
        fillTempTablesWithMain(evcGuid, callData.customerId);
        fillDiscountTemp(evcGuid, evcType, 0);

        //DiscountEvcPrizeDBAdapter.fill(evcGuid);
        DiscountOrderPrizeDBAdapter.UpdateOrderDiscount(callData.orderId);

        return evcGuid;
    }

    public static String fillEVC(DiscountCallReturnData callData, EVCType evcType, boolean isNew) {

        String evcGuid1 = UUID.randomUUID().toString().toUpperCase();
        String evcGuid2 = UUID.randomUUID().toString().toUpperCase();

        clearOldEVCData();

        fillEVCHeader(callData, evcGuid1, evcGuid2, evcType.value());
        fillEVCItem(callData, evcGuid1, evcGuid2, isNew);

        fillCustomerTemp(evcGuid1, evcType, callData.returnRefId);
        fillTempTablesWithXML(callData, evcGuid1);
        fillDiscountTemp(evcGuid1, evcType, callData.returnRefId);

        return evcGuid1 + ":" + evcGuid2;
    }

    private static void clearOldEVCData() {
        EVCHeaderSDSDBAdapter.getInstance().deleteAllEVCHeader();
        EVCItemSDSDBAdapter.getInstance().deleteAllEVCItem();
        EVCItemStatutesSDSDBAdapter.getInstance().deleteAllEVCItemStatutes();
        EVCTempCustomerSDSDBAdapter.getInstance().deleteAllEVCTemps();
        EVCTempGoodsGroupDetailSDSDBAdapter.getInstance().deleteAllEVCTempsById();
        EVCTempGoodsDetailSDSDBAdapter.getInstance().deleteAllEVCTemps();
        EVCTempGoodsMainSubTypeSDSDBAdapter.getInstance().deleteAllEVCTemps();
        EVCTempDiscountDBAdapter.getInstance().deleteAllEVCTemps();
        EVCTempCustomersMainSubTypeSDSDBAdapter.getInstance().deleteAllEVCTemps();
        DiscountEvcPrizeDBAdapter.getInstance().clearAllData();

        clearTempEVCData();
    }

    public static void clearTempEVCData() {
        EVCTempSDSDBAdapter.getInstance().deleteAllEVCTemps();
        EVCTempSummarySDSDBAdapter.getInstance().deleteAllEVCYTempSummaries();
        EVCTempSummaryFinalSDSDBAdapter.getInstance().deleteAllEVCYTempSummaries();

        EVCTempReturnItemSDSDBAdapter.getInstance().deleteAllEVCYTempReturns();
        EVCTempReturnItemSummarySDSDBAdapter.getInstance().deleteAllEVCYTempReturnSummeries();
        EVCTempReturnItemSummaryFinalSDSDBAdapter.getInstance().deleteAllEVCYTempReturnSummeryFinals();

        EVCTempGoodsCategorySDSDBAdapter.getInstance().deleteAllEVCTemps();
        EVCTempAcceptedDiscountAdapter.getInstance().deleteAllEVCTemps();
        EVCTempGoodsPackageItemSDSDBAdapter.getInstance().deleteAllEVCTemps();
        EVCTempOrderPrizeSDSDBAdapter.getInstance().clearAllData();
    }


    private static void fillEVCHeader(DiscountCallOrderData callData, String evcId, int evcTypeId, int disTypeId) {

        EVCHeaderSDSDBAdapter.getInstance().saveEVCHeader(callData, evcId, evcTypeId, disTypeId);
    }

    private static void fillEVCItem(DiscountCallOrderData callData, String evcId) {

        for (DiscountCallOrderLineData lineData : callData.callOrderLineItemData) {
            if (lineData.invoiceTotalQty.compareTo(BigDecimal.ZERO) == 1 && !lineData.isRequestPrizeItem)
                EVCItemSDSDBAdapter.getInstance().saveEVCItem(lineData, evcId);
        }
    }

    private static void fillEVCHeader(DiscountCallReturnData discountCallReturnData, String evcId1, String evcId2, int evcTypeId) {

        EVCHeaderSDSDBAdapter.getInstance().saveEVCHeader(discountCallReturnData, evcId1, evcId2, evcTypeId);
    }

    private static void fillEVCItem(DiscountCallReturnData callData, String evcId1, String evcId2, boolean isNew ) {

        EVCItemSDSDBAdapter.getInstance().saveEVCItem(callData, evcId1, evcId2, isNew);
    }

    private static void fillCustomerTemp(String evcId, EVCType evcType, int refId) {

        EVCTempCustomerSDSDBAdapter.getInstance().fillCustomerTemp(evcId, evcType, refId);
    }

    private static void fillDiscountTemp(String evcId, EVCType evcType, int refId) {
        EVCTempDiscountDBAdapter.getInstance().fillDiscountTemp(evcId, evcType, refId);
    }

    private static void fillTempTablesWithXML(DiscountCallReturnData returnData, String evcId) {

        if (returnData.returnType == ReturnType.WITHREF.value() || returnData.returnType == ReturnType.WITHREF_WITHOUT_ORDER.value()) {
            String xmls = DiscountCustomerOldInvoiceHeaderDBAdapter.getInstance().getReferenceXMLs(returnData.returnRefId);
            String[] arrays = xmls.split(",");
            if (arrays.length > 0) {
                String goodsGroupTreeXML, goodsDetailXML, goodsMainSubTypeXML;
                goodsGroupTreeXML = arrays[0];
                goodsDetailXML = arrays[1];
                goodsMainSubTypeXML = arrays[2];

                EVCTempGoodsGroupDetailSDSDBAdapter.getInstance().fillEVCTempByXml(goodsGroupTreeXML);
                EVCTempGoodsDetailSDSDBAdapter.getInstance().fillEVCTempByXml(goodsDetailXML);
                EVCTempGoodsMainSubTypeSDSDBAdapter.getInstance().fillEVCTempByXml(goodsMainSubTypeXML);
            }
        } else
            fillTempTablesWithMain(evcId, returnData.customerId);

    }

    private static void fillTempTablesWithMain(String evcId, int custref) {
        EVCTempGoodsGroupDetailSDSDBAdapter.getInstance().fillEVCTempByMainTable();
        EVCTempGoodsDetailSDSDBAdapter.getInstance().fillEVCTempByMainTable(evcId);
        EVCTempGoodsMainSubTypeSDSDBAdapter.getInstance().fillEVCTempByMainTable(evcId);
        if (GlobalVariables.getBackOfficeVersion() == BackOfficeVersion.SDS19)
            EVCTempCustomersMainSubTypeSDSDBAdapter.getInstance().fillEVCTempByMainTable(evcId, custref);
    }
}
