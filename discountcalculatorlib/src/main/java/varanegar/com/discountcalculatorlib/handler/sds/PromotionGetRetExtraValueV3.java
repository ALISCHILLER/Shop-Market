package varanegar.com.discountcalculatorlib.handler.sds;


import java.math.BigDecimal;

import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemStatutesSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempRemPrizeSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempReturnItemSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempReturnItemSummaryFinalSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempReturnItemSummarySDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EvcTempDecreasePrizeSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EvcTempReturnPrizeSDSDBAdapter;
import varanegar.com.discountcalculatorlib.handler.PromotionHandlerV3;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;

import static android.icu.text.MessagePattern.ArgType.SELECT;

public class PromotionGetRetExtraValueV3 {

    public static class EvcHeaderData {
        private int disType;

        private BigDecimal dis1;

        private BigDecimal dis2;

        private BigDecimal dis3;

        private BigDecimal add1;

        private BigDecimal add2;
        private int saleRef;

        public EvcHeaderData(int disType, BigDecimal dis1, BigDecimal dis2, BigDecimal dis3, BigDecimal add1, BigDecimal add2, int SaleRef) {
            this.disType = disType;
            this.dis1 = dis1;
            this.dis2 = dis2;
            this.dis3 = dis3;
            this.add1 = add1;
            this.add2 = add2;
            this.saleRef = saleRef;
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

        public BigDecimal getDis2() {
            return dis2;
        }

        public void setDis2(BigDecimal dis2) {
            this.dis2 = dis2;
        }

        public BigDecimal getDis3() {
            return dis3;
        }

        public void setDis3(BigDecimal dis3) {
            this.dis3 = dis3;
        }

        public BigDecimal getAdd1() {
            return add1;
        }

        public void setAdd1(BigDecimal add1) {
            this.add1 = add1;
        }

        public BigDecimal getAdd2() {
            return add2;
        }

        public void setAdd2(BigDecimal add2) {
            this.add2 = add2;
        }
        //endregion getter and setter methods
    }

    /*SLE.usp_GetRetExtraValue*/
    public static void getRetExtraValue(String saleEVCId, String newSaleEVCId, String callUniqueId, int orderId, String orderDate, int saleId, int orderNo, int dcRef) throws DiscountException {

        //SLE.usp_GetRetExtraValue_CalcPrizeForGoodsGroup @SaleEVCID, @NewSaleEVCID
        EVCItemSDSDBAdapter.getInstance().GetRetExtraValue_CalcPrizeForGoodsGroup(saleEVCId, newSaleEVCId);

        EvcHeaderData headerData = EVCHeaderSDSDBAdapter.getInstance().getReturnEvcHeaderData(newSaleEVCId);

        int prizeCalcType =  DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getPrizeCalcType(saleId);

        //Comment for 4.16.73
        //deleteInvalidReturnItem(newSaleEVCId);

        EVCTempRemPrizeSDSDBAdapter.getInstance().fillTemp(saleId);
        PromotionHandlerV3.calcPromoIntral(saleEVCId, callUniqueId, orderId, EVCType.SELLRETURN.value(), orderDate, "", saleId, orderNo, dcRef); //ordertyp ???
        EVCItemSDSDBAdapter.getInstance().updateByRemPrize(saleEVCId);
        PromotionHandlerV3.calcPromoIntral(newSaleEVCId, callUniqueId, orderId, EVCType.SELLRETURN.value(), orderDate, "", saleId, orderNo, dcRef); //ordertyp ???

        EvcTempReturnPrizeSDSDBAdapter.getInstance().fillTemp(saleEVCId,newSaleEVCId);
        EvcTempDecreasePrizeSDSDBAdapter.getInstance().fillTemp();
        EVCItemSDSDBAdapter.getInstance().updateEVCitemFromDecreasePrize(newSaleEVCId);
        EVCItemSDSDBAdapter.getInstance().updateEVCitemTotalQty1(saleEVCId,newSaleEVCId);
        EVCItemSDSDBAdapter.getInstance().updateEVCitemTotalQty2(saleEVCId,newSaleEVCId);

        updateEVCItem1(newSaleEVCId);
        EVCItemStatutesSDSDBAdapter.getInstance().deleteUnAffectedEVCItemStatutes(newSaleEVCId);

         validateHeaderData(headerData);

        updateHeaderData(newSaleEVCId, 10, headerData.dis1.doubleValue(), headerData.dis2.doubleValue(), headerData.dis3.doubleValue(), headerData.add1.doubleValue(), headerData.add2.doubleValue());

        PromotionHandlerV3.calcPromoIntral(newSaleEVCId, callUniqueId, orderId, EVCType.SELLRETURN.value(), orderDate, "", saleId, orderNo, dcRef); //ordertyp ???

        changeEVCType(newSaleEVCId, 2);

        /* sle.usp_GetRetExtraValue_Result*/
        getReturnExtraValues(saleEVCId, newSaleEVCId, prizeCalcType);
    }

    private static void deleteInvalidReturnItem(String evcId) {
        EVCItemSDSDBAdapter.getInstance().deleteInvalidReturnItem(evcId);
    }

    private static void updateEVCItem1(String evcId) {
        EVCItemSDSDBAdapter.getInstance().updateEVCitemForSellReturn1(evcId);
    }

    private static void validateHeaderData(EvcHeaderData headerData) {

        if (headerData.getDisType() == 2)
        {
            headerData.setDis1(BigDecimal.ZERO);
            headerData.setDis2(BigDecimal.ZERO);
            headerData.setDis3(BigDecimal.ZERO);
            headerData.setAdd1(BigDecimal.ZERO);
            headerData.setAdd2(BigDecimal.ZERO);
        }
    }

    private static void updateHeaderData(String evcId, int evcType, double dis1, double dis2, double dis3, double add1, double add2)
    {
        EVCHeaderSDSDBAdapter.getInstance().updateEVCHeaderForSellReturn(evcId, evcType, dis1, dis2, dis3, add1, add2);
    }

    private static void changeEVCType(String evcId, int evcType)
    {
        EVCHeaderSDSDBAdapter.getInstance().updateEVCType(evcId, evcType);
    }

    /* sle.usp_GetRetExtraValue_Result*/
    private static void getReturnExtraValues(String saleEvcId, String newSaleEVCid, int prizeCalcType)
    {
        EVCTempReturnItemSDSDBAdapter.getInstance().fillEVCTempReturnItem(saleEvcId, newSaleEVCid, prizeCalcType);
       //TODO Asal ta inja
        /*
        select rm.GoodsRef, TotalQty-RemPrizeQty as MoreQty
        into #MorePrize
        from #tmpTable tt
        inner join (
            select si.GoodsRef, si.TotalQty - isnull(SUM(ri.TotalQty), 0) as RemPrizeQty
        from sle.tblSaleItm si
        left join sle.tblRetSaleHdr rh on rh.TSaleRef=si.HdrRef and rh.CancelFlag=0
        left join sle.tblRetSaleItm ri on ri.HdrRef=rh.Id and si.GoodsRef=ri.GoodsRef and si.PrizeType=ri.PrizeType
        where si.HdrRef=@SaleRef and si.PrizeType=1
        group by si.GoodsRef, si.TotalQty
  ) rm on rm.GoodsRef=tt.GoodsRef
        where tt.PrizeType=1
        and TotalQty>RemPrizeQty
        if exists (select 1 from #MorePrize)
        begin
        update tt
        set TotalQty = TotalQty + mp.MoreQty
        from #tmpTable tt
        inner join #MorePrize mp on mp.GoodsRef=tt.GoodsRef and isnull(FreeReasonId, 0)=0
        where tt.PrizeType=0

        delete tt
        from #tmpTable tt
        inner join #MorePrize mp on mp.GoodsRef=tt.GoodsRef
        where tt.PrizeType=1

*/
        EVCTempReturnItemSDSDBAdapter.getInstance().update1(saleEvcId);

        EVCTempReturnItemSDSDBAdapter.getInstance().update2(saleEvcId, newSaleEVCid);
        EVCTempReturnItemSDSDBAdapter.getInstance().update3(saleEvcId, newSaleEVCid);

        EVCTempReturnItemSummarySDSDBAdapter.getInstance().fillEVCTempReturnItemSummary(newSaleEVCid);

        EVCTempReturnItemSummaryFinalSDSDBAdapter.getInstance().fillEVCTempReturnItemSummaryFinal(newSaleEVCid);
        EVCTempReturnItemSummaryFinalSDSDBAdapter.getInstance().updateDiscountAndAddAmount(newSaleEVCid);

    }
}
