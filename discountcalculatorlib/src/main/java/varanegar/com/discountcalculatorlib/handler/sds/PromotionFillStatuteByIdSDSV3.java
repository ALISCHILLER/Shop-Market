package varanegar.com.discountcalculatorlib.handler.sds;


import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemStatutesSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempDiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempSummary3SDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempSummaryFinalSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempSummarySDSDBAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;

public class PromotionFillStatuteByIdSDSV3 {

    /*usp_FillEVCStatuteByID*/
    public static void fillStatuteById(String evcId, int orderId, String orderDate, String orderTypeId, int saleId, int orderNo) throws DiscountException {
        EVCTempSummaryFinalSDSDBAdapter.getInstance().deleteAllEVCYTempSummarieById(evcId);
        int evcType = EVCHeaderSDSDBAdapter.getInstance().getEVCType(evcId);

        fetchCustomerRefInfo();

        /*SLE.usp_FillEVCStatuteByID_FilterByEvcInfo*/
        if(evcType == 2 || evcType == 10 || evcType == 12) //and @InputDiscountRef=0
        {
            deleteInvalidDiscountForSellReturn(evcId, saleId);
        }
        extractValidDiscount(evcId, orderId, orderDate, orderTypeId, saleId, evcType, orderNo);


        /*usp_FillEvcStatuteByID_ApplyDiscountCriteria*/
        FillEvcStatuteByID_ApplyDiscountCriteria(evcId);

        int evcItemStatutesExists=0;


        if (EVCTempSummarySDSDBAdapter.getInstance().ExistsData(evcId))
            evcItemStatutesExists=1;

        EVCTempSummarySDSDBAdapter.getInstance().InsertGoodRef(evcId, saleId);
        EVCTempSummarySDSDBAdapter.getInstance().InsertSkipGoodsRef(evcId, saleId);
        int LoopCount = EVCTempSummarySDSDBAdapter.getInstance().GetLoopCount();

        int counter=0;
        while (evcItemStatutesExists == 1 && counter<LoopCount){

            EVCTempSummary3SDSDBAdapter.getInstance().insert(evcId);
            EVCTempSDSDBAdapter.getInstance().deletePriority(evcId);

            EVCTempSummaryFinalSDSDBAdapter.getInstance().insertFromTemp3(evcId);
            EVCTempSummaryFinalSDSDBAdapter.getInstance().deleteByEvcRef(evcId);

            EVCTempSDSDBAdapter.getInstance().deleteByTemp3(evcId);
            FillEvcStatuteByID_ApplyDiscountCriteria(evcId);

            EVCTempSummaryFinalSDSDBAdapter.getInstance().deleteEvcSkipDiscount(evcId, saleId);
            EVCTempSummary3SDSDBAdapter.getInstance().delete(evcId);

            if (!EVCTempSummarySDSDBAdapter.getInstance().ExistsData(evcId))
                evcItemStatutesExists=0;
            else
                evcItemStatutesExists=1;
            counter++;
        }

        /*SLE.usp_FillEVCStatuteByID*/
        fillEVCItemStatute(evcId);
        deleteInvalidItemStatuse(evcId);
    }

    public static String CheckValidEvcStatutes(String evcId) throws DiscountException {
        return EVCHeaderSDSDBAdapter.getInstance().CheckValidEvcStatutes(evcId);
    }

    private static void fetchCustomerRefInfo() {
        //No need in tablet
    }

    private static void deleteInvalidDiscountForSellReturn(String evcId, int saleId)
    {
        EVCTempDiscountDBAdapter.getInstance().deleteInvalidDiscountForSellReturn(evcId, saleId);
    }

    private static void extractValidDiscount(String evcId, int orderId, String orderDate, String orderTypeId, int saleId, int evcType, int orderNo) {

		EVCTempSDSDBAdapter.getInstance().fillEVCTemp(evcId, orderId, orderDate, orderTypeId, saleId, evcType, orderNo);
    }

    /*usp_FillEvcStatuteByID_ApplyDiscountCriteria*/
    private static void FillEvcStatuteByID_ApplyDiscountCriteria(String evcId) throws DiscountException {

        EVCTempSummarySDSDBAdapter.getInstance().fillEVCItemSummary(evcId);
    }

    private static void fillEVCItemStatute(String evcId) {

        EVCItemStatutesSDSDBAdapter.getInstance().fillEVCTemp(evcId);
    }

    private static void deleteInvalidItemStatuse(String evcId) {

        EVCItemStatutesSDSDBAdapter.getInstance().deleteInvalidItemStatuse(evcId);
    }


}
