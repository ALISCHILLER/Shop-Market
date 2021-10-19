package varanegar.com.discountcalculatorlib.handler.vnlite;

import java.math.BigDecimal;

import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemStatutesVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempReturnItemSummaryFinalVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempReturnItemSummaryVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempReturnItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;

public class PromotionGetRetExtraValueVnLiteV3 {

    public static class EvcHeaderData {
        private int disType;

        private BigDecimal dis1;

        private BigDecimal add1;

        public EvcHeaderData(int disType, BigDecimal dis1, BigDecimal add1) {
            this.disType = disType;
            this.dis1 = dis1;
            this.add1 = add1;
        }

        //region getter and setter methods
        public int getDisType() {
            return disType;
        }

        public void setDisType(int disType) {
            this.disType = disType;
        }

        public BigDecimal getDis1() {
            return dis1;
        }

        public void setDis1(BigDecimal dis1) {
            this.dis1 = dis1;
        }

        public BigDecimal getAdd1() {
            return add1;
        }

        public void setAdd1(BigDecimal add1) {
            this.add1 = add1;
        }
    }

    /*[usp_GetRetExtraValue]*/
    public static void getRetExtraValue(String saleEVCId, String newSaleEVCId, String callUniqueId, int orderNo, String date) throws DiscountException {

        EvcHeaderData headerData = EVCHeaderVnLiteDBAdapter.getInstance().getReturnEvcHeaderData(newSaleEVCId);

        deleteInvalidReturnItem(newSaleEVCId);

        PromotionDoEVCByStatuteTableVnLiteV3.doEVCByStatuteTable(saleEVCId, callUniqueId, EVCType.SELLRETURN.value(), date);

        EVCItemVnLiteDBAdapter.getInstance().deleteInvalidQty(newSaleEVCId);
        EVCItemVnLiteDBAdapter.getInstance().updatePrizeGoodMainGood();

        /*
        insert into #EvcDetail (EvcId, ProductId, Qty)
        select EvcId, ProductId, Qty from EvcDetail
        where EvcId=@NewSaleEVCID and IsPrize=0
        */
        PromotionDoEVCByStatuteTableVnLiteV3.doEVCByStatuteTable(newSaleEVCId, callUniqueId, EVCType.SELLRETURN.value(), date);

        boolean e = EVCItemVnLiteDBAdapter.getInstance().ExistPrizeProductInMainProduct(newSaleEVCId);
        if (e)
        {
            updateEVCItem1(newSaleEVCId);
            EVCItemStatutesVnLiteDBAdapter.getInstance().deleteExist(newSaleEVCId);

            validateHeaderData(headerData);

            updateHeaderData(newSaleEVCId, 10, headerData.dis1.doubleValue(), headerData.add1.doubleValue());

            PromotionDoEVCByStatuteTableVnLiteV3.doEVCByStatuteTable(newSaleEVCId, callUniqueId, EVCType.SELLRETURN.value(), date);

            changeEVCType(newSaleEVCId, 2);
        }

        /*new code*/
        EVCItemVnLiteDBAdapter.getInstance().resetEvc2(saleEVCId, newSaleEVCId);
        /*new code*/
        getReturnExtraValues(saleEVCId, newSaleEVCId);
    }

    private static void deleteInvalidReturnItem(String evcId) {
        EVCItemVnLiteDBAdapter.getInstance().deleteInvalidReturnItem(evcId);
    }

    private static void updateEVCItem1(String evcId) {
        EVCItemVnLiteDBAdapter.getInstance().updateEVCitemForSellReturn1(evcId);
    }

    private static void validateHeaderData(EvcHeaderData headerData) {

        if (headerData.getDisType() == 2) {
            headerData.setDis1(BigDecimal.ZERO);
            headerData.setAdd1(BigDecimal.ZERO);
        }
    }

    private static void updateHeaderData(String evcId, int evcType, double dis, double add) {
        EVCHeaderVnLiteDBAdapter.getInstance().updateEVCHeaderForSellReturn(evcId, evcType, dis, add);
    }

    private static void changeEVCType(String evcId, int evcType) {
        EVCHeaderVnLiteDBAdapter.getInstance().updateEVCType(evcId, evcType);
    }

    /*[usp_GetRetExtraValue]*/
    private static void getReturnExtraValues(String saleEvcId, String newSaleEVCid) {
        EVCTempReturnItemVnLiteDBAdapter.getInstance().fillEVCTempReturnItem(saleEvcId, newSaleEVCid);
        EVCTempReturnItemVnLiteDBAdapter.getInstance().update1(saleEvcId, newSaleEVCid);
/*TODO
        insert into SellReturnReduceOfQty (SellReturnId ,ProductId ,Qty,BatchId )
        select @SellReturnId,ProductId,Qty,DetailId from #tmpTable where ReduceOfQty=1
*/
        //EVCTempReturnItemVnLiteDBAdapter.getInstance().update2(saleEvcId, newSaleEVCid);
        EVCTempReturnItemVnLiteDBAdapter.getInstance().resetUnitPrice(saleEvcId, newSaleEVCid);
        EVCTempReturnItemSummaryVnLiteDBAdapter.getInstance().fillEVCTempReturnItemSummary(newSaleEVCid);

        EVCTempReturnItemSummaryFinalVnLiteDBAdapter.getInstance().fillEVCTempReturnItemSummaryFinal(newSaleEVCid);
        EVCTempReturnItemSummaryFinalVnLiteDBAdapter.getInstance().updateDiscountAndAddAmount(newSaleEVCid);
    }

    /*usp_ResetEVC*/
    public static void resetEVC(String evcId){
        EVCItemVnLiteDBAdapter.getInstance().deletePrize(evcId);
        EVCItemVnLiteDBAdapter.getInstance().resetEvc(evcId);
        EVCItemStatutesVnLiteDBAdapter.getInstance().deleteAllEVCItemStatutesById(evcId);
        EVCHeaderVnLiteDBAdapter.getInstance().resetEvc(evcId);


    }

}
