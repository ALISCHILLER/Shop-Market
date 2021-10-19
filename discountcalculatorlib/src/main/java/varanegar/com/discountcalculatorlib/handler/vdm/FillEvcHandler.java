package varanegar.com.discountcalculatorlib.handler.vdm;

import java.math.BigDecimal;
import java.util.ArrayList;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.dataadapter.general.DiscountFreeReasonDBAdapter;
import varanegar.com.discountcalculatorlib.entity.general.DiscountFreeReason;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.vdmclient.call.CalcData;
import varanegar.com.vdmclient.call.EvcHeader;
import varanegar.com.vdmclient.call.EvcItem;

public class FillEvcHandler {


    public static CalcData fillCalcData(DiscountCallOrderData discountData, EVCType evcType) {
        Timber.d("new version discount : fillEVC started ");
        CalcData calcData = new CalcData();

        fillEVCHeader(calcData , discountData, evcType.value());
        fillEVCItem(calcData , discountData);

        return calcData;
    }

    private static void fillEVCHeader(CalcData calcData,
                                      DiscountCallOrderData discountData,
                                      int evcType){
        ArrayList<EvcHeader> headers = new ArrayList<>();
        EvcHeader header = new EvcHeader();

        //discountData.dealerName

        header.Id = 1;
        header.CustRef = discountData.customerId;
        header.TotalAmount = BigDecimal.ZERO;
        header.Dis1 = BigDecimal.ZERO;
        header.Dis2 = BigDecimal.ZERO;
        header.Dis3 = BigDecimal.ZERO;
        header.Add1 = BigDecimal.ZERO;
        header.Add2 = BigDecimal.ZERO;
        header.Tax = BigDecimal.ZERO;
        header.Charge = BigDecimal.ZERO;
        header.OtherDiscount = BigDecimal.ZERO;
        header.OtherAddition = BigDecimal.ZERO;
        header.OrderType = discountData.OrderType;
        header.PayType = discountData.invoicePaymentTypeId;
        header.CallId = discountData.callUniqueId;
        header.RefId = discountData.saleId;
        header.EvcId = discountData.evcRef;
        //public Entity.EvcType EvcType;
        header.EvcTypeValue = evcType;
        //public Entity.DiscountType DisType;
        header.DiscountTypeValue = discountData.disTypeId;
        header.DcRef = discountData.DCRef;
        header.DcSaleOfficeRef = discountData.DcSaleOfficeRef;
        header.SaleOfficeRef = discountData.SaleOfficeRef;
        header.DcCode = discountData.dcCode;
        header.DateOf = discountData.saleDate;
        //???
        header.OprDate  = discountData.saleDate;
        header.EvcDate = discountData.saleDate;
        header.AccYear = discountData.accYear;
        header.StockDcRef = discountData.stockDCRef;
        header.StockDcCode = discountData.stockDCCode;
        header.DealerRef = discountData.dealerRef;
        header.DealerCode = discountData.dealerCode;
        header.SupervisorRef = discountData.supervisorRef;
        header.SupervisorCode = discountData.supervisorCode;
        header.SalesGoodsDetailXML = null;
        header.SalesGoodsGroupTreeXML = null;
        header.SalesGoodsMainSubTypeDetailXML = null;
        header.SalesCustomerMainSubTypeDetailXML = null;
        header.StockDCName = discountData.stockDCName;
        header.SupervisorName = discountData.supervisorName;
        header.SaleRef = discountData.saleId;
        header.OrderNo = discountData.orderNo;
//new
        header.PaymentUsanceRef = discountData.PaymentUsanceRef;
        header.PaymentUsanceName = discountData.PaymentUsanceName;
        header.TOrderRef = discountData.TOrderRef;
        header.TOrderNo = discountData.TOrderNo;
        header.BuyTypeRef = discountData.BuyTypeRef;
        header.UsanceDay = discountData.UsanceDay;
        header.OrderRef = discountData.OrderRef ;
        header.DCName = discountData.DCName;
        header.PayTypeName = discountData.PayTypeName;
        header.DisTypeName = discountData.DisTypeName;
        header.OrderTypeName = discountData.OrderTypeName;
        header.SaleOfficeName = discountData.SaleOfficeName;

        headers.add(header);

        calcData.EvcHeaders = headers;
    }

    private static void fillEVCItem(CalcData calcData, DiscountCallOrderData discountData){
        ArrayList<EvcItem> items = new ArrayList<>();
        int i = 0;
        for (DiscountCallOrderLineData line : discountData.callOrderLineItemData) {
            i++;

            int calcType = 0;
            if (line.freeReasonId != 0) {
                DiscountFreeReason discountFreeReason = DiscountFreeReasonDBAdapter.getInstance().getFreeReasonById(line.freeReasonId);
                if (discountFreeReason != null)
                    calcType = discountFreeReason.calcPriceType;
            }


            EvcItem item = new EvcItem();
            item.Id = i;
            item.RowOrder = line.sortId;
            item.GoodsRef = line.productId;
            item.GoodsCtgrRef = line.GoodsCtgrRef;
            item.TotalQty = line.invoiceTotalQty.intValue();
            item.UnitCapasity = 1 ;
            if (line.weight != null)
                item.TotalWeight = line.weight.longValue();


            if (calcType == 2){
                item.Amount = BigDecimal.ZERO;
                item.CustPrice = BigDecimal.ZERO;
                item.UserPrice = BigDecimal.ZERO;
                //item.AmountNut = BigDecimal.ZERO;
            }
            else{
                if (GlobalVariables.getDecimalDigits() > 0) {
                    BigDecimal cartonType = new BigDecimal(line.cartonType * 1.0);
                    //item.AmountNut =  Math.round(Math.round((unitPrice.doubleValue() * cartonType) * 1.0) * (totalQty.longValue() / (cartonType * 1.0))));
                    item.Amount = new BigDecimal(Math.round(Math.round(line.unitPrice.multiply(cartonType).doubleValue())
                            * (line.invoiceTotalQty.divide(cartonType)).doubleValue()) );
                } else {
                    //item.KEY_AMOUNT_NUT = line.unitPrice.multiply(line.invoiceTotalQty);
                    item.Amount = line.unitPrice.multiply(line.invoiceTotalQty);
                }
                item.CustPrice = line.unitPrice;
                item.UserPrice = line.userprice;
            }

                item.Discount = BigDecimal.ZERO;
            //    public decimal AmountNut
            //discountData.NetAmount { get { return AmountNut; } }
            //discountData.UnitPrice { get { return CustPrice; } }
            item.PrizeType = 0;
            item.SupAmount = BigDecimal.ZERO;
            item.AddAmount = BigDecimal.ZERO;
            item.CPriceRef = new Long(line.priceId).intValue();
            item.PriceRef = new Long(line.priceId).intValue();
            item.ChargePercent = BigDecimal.ZERO;
            item.TaxPercent = BigDecimal.ZERO;
            item.UnitQty = line.invoiceBigQty;
            item.UnitRef = line.invoiceBigQtyId;
            item.EvdId = line.evcId;
            item.EvcRef= i ;
            item.CallId = line.callUniqueId;
            item.Volume  = 0;
            item.FreeReasonId = line.freeReasonId;
            item.Tax = BigDecimal.ZERO;
            item.Charge = BigDecimal.ZERO;
            item.DisRef = 0;
            item.PeriodicDiscountRef = 0;
            item.EvcItemDis1 = BigDecimal.ZERO;
            item.EvcItemDis2 = BigDecimal.ZERO;
            item.EvcItemDis3 = BigDecimal.ZERO;
            item.EvcItemAdd1 = BigDecimal.ZERO;
            item.EvcItemAdd2 = BigDecimal.ZERO;
            item.EvcItemOtherDiscount = BigDecimal.ZERO;
            item.EvcItemOtherAddition = BigDecimal.ZERO;
            item.SalePrice = line.unitPrice;
            items.add(item);

        }
        calcData.EvcItems = items;

    }
}