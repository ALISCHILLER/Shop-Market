package varanegar.com.discountcalculatorlib.viewmodel;

import android.annotation.SuppressLint;

import com.varanegar.framework.base.VaranegarApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProduct;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

/**
 * Created by m.aghajani on 10/4/2015.
 */
public class DiscountCallOrderLineData {
    public UUID orderLineId; //bahar
    public int customerId;
    public String orderUniqueId;
    public int productId;
    public String productName;
    public String productCode;
    public boolean isRequestPrizeItem;
    public BigDecimal requestTotalQty;
    public BigDecimal requestBigQty;
    public long requestBigQtyId;
    public String requestBigQtyName;
    public BigDecimal requestSmallQty;
    public long requestSmallQtyId;
    public String requestSmallQtyName;
    public BigDecimal totalRequestAmount;
    public BigDecimal totalRequestAdd1Amount;
    public BigDecimal totalRequestAdd2Amount;
    public BigDecimal totalRequestDiscount;
    public BigDecimal totalRequestTax;
    public BigDecimal totalRequestCharge;
    public BigDecimal totalRequestNetAmount;
    public int sortId;
    public int indexInfo;
    public String callUniqueId;
    public BigDecimal weight;
    public long priceId;
    public BigDecimal unitPrice;

    public BigDecimal amount;
    public BigDecimal custPrice;
    public BigDecimal invoiceTotalQty;
    public BigDecimal invoiceBigQty;
    public BigDecimal TakhfifatKol;
    public BigDecimal AmountNutPT03;
    public BigDecimal Fee;
    public BigDecimal FeeKol;
    public BigDecimal totalQty;
    public String zterm;
    public long invoiceBigQtyId;
    public String invoiceBigQtyName;
    public BigDecimal invoiceSmallQty;
    public long invoiceSmallQtyId;
    public String invoiceSmallQtyName;
    public BigDecimal totalInvoiceAmount;
    public BigDecimal totalInvoiceAdd1Amount;
    public BigDecimal evcItemAdd;
    public BigDecimal totalInvoiceAdd2Amount;
    public BigDecimal totalInvoiceDiscount;
    public BigDecimal totalInvoiceTax;
    public BigDecimal totalInvoiceCharge;
    public BigDecimal totalInvoiceNetAmount;
    public String comment;
    public int cartonType;
    public BigDecimal userprice;
    public String evcId;
    public BigDecimal totalRequestDis1Amount;
    public BigDecimal totalRequestDis2Amount;
    public BigDecimal totalRequestDis3Amount;
    public BigDecimal invoiceDis1;
    public BigDecimal invoiceDis2;
    public BigDecimal invoiceDis3;
    public BigDecimal invoiceDisOther;
    public BigDecimal totalInvoiceAddOther;
    public BigDecimal adjustmentPrice;

    public BigDecimal cashDiscount;
    public BigDecimal chequeDiscount;

    public int volume;
    public int freeReasonId;
    public int unitCapacity;

    public int isFreeItem;
    public int disRef;

    public BigDecimal invoiceBulkQty;

    public int PayDuration;
    public int RuleNo;

    //new
    public int GoodsCtgrRef;

    public ArrayList<CallOrderLineItemStatusData> callOrderLineItemStatusDatas;
    public String saleNo;
    public String referenceNo;
    public UUID returnReasonId;
    public String orderDate;
    public String saleS_ITEM ;
    public BigDecimal amountCash;
    public BigDecimal amountCheque;
    public BigDecimal amountNutCash;
    public BigDecimal amountNutCheque;
    public BigDecimal amountImmediate;
    public BigDecimal amountNutImmediate;

    public String cart ;
    public DiscountCallOrderLineData() {
    }


    public DiscountCallOrderLineData(int customerId, String orderUniqueId, int productId, String productName, String productCode
            , int isRequestPrizeItem, BigDecimal requestTotalQty, BigDecimal requestBigQty, int requestBigQtyId, String requestBigQtyName
            , BigDecimal requestSmallQty, int requestSmallQtyId, String requestSmallQtyName, BigDecimal totalRequestAmount
            , BigDecimal totalRequestAdd1Amount, BigDecimal totalRequestAdd2Amount, BigDecimal totalRequestDiscount
            , BigDecimal totalRequestTax, BigDecimal totalRequestCharge, BigDecimal totalRequestNetAmount, int sortId, int indexInfo
            , String callUniqueId, BigDecimal weight, int priceId, BigDecimal unitPrice, BigDecimal invoiceTotalQty, BigDecimal invoiceBigQty
            , int invoiceBigQtyId, String invoiceBigQtyName, BigDecimal invoiceSmallQty, int invoiceSmallQtyId, String invoiceSmallQtyName
            , BigDecimal totalInvoiceAmount, BigDecimal totalInvoiceDiscount, BigDecimal totalInvoiceTax, BigDecimal totalInvoiceCharge, BigDecimal totalInvoiceNetAmount
            , String comment, int cartonType, BigDecimal userprice, String evcId, BigDecimal invoiceDis1, BigDecimal invoiceDis2, BigDecimal invoiceDis3
            , BigDecimal totalInvoiceAdd1Amount, BigDecimal totalInvoiceAdd2Amount, int volume, BigDecimal totalRequestDis1Amount, BigDecimal totalRequestDis2Amount
            , BigDecimal totalRequestDis3Amount, int freeReasonId) {
        this.customerId = customerId;
        this.orderUniqueId = orderUniqueId;
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.isRequestPrizeItem = isRequestPrizeItem == 1;
        this.requestTotalQty = requestTotalQty;
        this.requestBigQty = requestBigQty;
        this.requestBigQtyId = requestBigQtyId;
        this.requestBigQtyName = requestBigQtyName;
        this.requestSmallQty = requestSmallQty;
        this.requestSmallQtyId = requestSmallQtyId;
        this.requestSmallQtyName = requestSmallQtyName;
        this.totalRequestAmount = totalRequestAmount;
        this.totalRequestAdd1Amount = totalRequestAdd1Amount;
        this.totalRequestAdd2Amount = totalRequestAdd2Amount;
        this.totalRequestDiscount = totalRequestDiscount;
        this.totalRequestTax = totalRequestTax;
        this.totalRequestCharge = totalRequestCharge;
        this.totalRequestNetAmount = totalRequestNetAmount;
        this.sortId = sortId;
        this.indexInfo = indexInfo;
        this.callUniqueId = callUniqueId;
        this.weight = weight;
        this.priceId = priceId;
        this.unitPrice = unitPrice;
        this.invoiceTotalQty = invoiceTotalQty;
        this.invoiceBigQty = invoiceBigQty;
        this.invoiceBigQtyId = invoiceBigQtyId;
        this.invoiceBigQtyName = invoiceBigQtyName;
        this.invoiceSmallQty = invoiceSmallQty;
        this.invoiceSmallQtyId = invoiceSmallQtyId;
        this.invoiceSmallQtyName = invoiceSmallQtyName;
        this.totalInvoiceAmount = totalInvoiceAmount;
        this.totalInvoiceDiscount = totalInvoiceDiscount;
        this.totalInvoiceTax = totalInvoiceTax;
        this.totalInvoiceCharge = totalInvoiceCharge;
        this.totalInvoiceNetAmount = totalInvoiceNetAmount;
        this.comment = comment;
        this.cartonType = cartonType;
        this.userprice = userprice;
        this.evcId = evcId;
        this.invoiceDis1 = invoiceDis1;
        this.invoiceDis2 = invoiceDis2;
        this.invoiceDis3 = invoiceDis3;
        this.totalInvoiceAdd1Amount = totalInvoiceAdd1Amount;
        this.totalInvoiceAdd2Amount = totalInvoiceAdd2Amount;

        this.totalRequestDis1Amount = totalRequestDis1Amount;
        this.totalRequestDis2Amount = totalRequestDis2Amount;
        this.totalRequestDis3Amount = totalRequestDis3Amount;

        this.volume = volume;

        this.freeReasonId = freeReasonId;
    }

    //region Action methods

    public void ResetValues() {
        this.invoiceTotalQty = this.requestTotalQty;
        this.invoiceBigQty = this.requestBigQty;
        this.invoiceBigQtyId = this.requestBigQtyId;
        this.invoiceBigQtyName = this.requestBigQtyName;
        this.invoiceSmallQty = this.requestSmallQty;
        this.invoiceSmallQtyId = this.requestSmallQtyId;
        this.invoiceSmallQtyName = this.requestSmallQtyName;
        this.totalInvoiceAmount = this.totalRequestAmount;
        this.totalInvoiceDiscount = this.totalRequestDiscount;
        this.totalInvoiceTax = this.totalRequestTax;
        this.totalInvoiceCharge = this.totalRequestCharge;
        this.totalInvoiceNetAmount = this.totalRequestNetAmount;
        this.invoiceDis1 = this.totalRequestDis1Amount;
        this.invoiceDis2 = this.totalRequestDis2Amount;
        this.invoiceDis3 = this.totalRequestDis3Amount;
        this.totalInvoiceAdd1Amount = this.totalRequestAdd1Amount;
        this.totalInvoiceAdd2Amount = this.totalRequestAdd2Amount;
    }

    public void RejectRequest() {
        this.invoiceTotalQty = BigDecimal.ZERO;
        this.invoiceBigQty = BigDecimal.ZERO;
        this.invoiceBigQtyId = this.requestBigQtyId;
        this.invoiceBigQtyName = this.requestBigQtyName;
        this.invoiceSmallQty = BigDecimal.ZERO;
        this.invoiceSmallQtyId = this.requestSmallQtyId;
        this.invoiceSmallQtyName = this.requestSmallQtyName;
        this.totalInvoiceAmount = BigDecimal.ZERO;
        this.totalInvoiceDiscount = BigDecimal.ZERO;
        this.totalInvoiceTax = BigDecimal.ZERO;
        this.totalInvoiceCharge = BigDecimal.ZERO;
        this.totalInvoiceNetAmount = BigDecimal.ZERO;
        this.invoiceDis1 = BigDecimal.ZERO;
        this.invoiceDis2 = BigDecimal.ZERO;
        this.invoiceDis3 = BigDecimal.ZERO;
        this.totalInvoiceAdd1Amount = BigDecimal.ZERO;
        this.totalInvoiceAdd2Amount = BigDecimal.ZERO;
        this.totalInvoiceAdd1Amount = BigDecimal.ZERO;
        this.totalInvoiceAdd2Amount = BigDecimal.ZERO;
    }

    public DiscountCallOrderLineData copyForWithPromo() {
        DiscountCallOrderLineData copyLine = new DiscountCallOrderLineData();

        copyLine.customerId = customerId;
        copyLine.orderUniqueId = orderUniqueId;
        copyLine.productId = productId;
        copyLine.productName = productName;
        copyLine.productCode = productCode;
        copyLine.isRequestPrizeItem = isRequestPrizeItem;
        copyLine.requestTotalQty = requestTotalQty;
        copyLine.requestBigQty = requestBigQty;
        copyLine.requestBigQtyId = requestBigQtyId;
        copyLine.requestBigQtyName = requestBigQtyName;
        copyLine.requestSmallQty = requestSmallQty;
        copyLine.requestSmallQtyId = requestSmallQtyId;
        copyLine.requestSmallQtyName = requestSmallQtyName;
        copyLine.totalRequestAmount = totalRequestAmount;
        copyLine.totalRequestAdd1Amount = totalRequestAdd1Amount;
        copyLine.totalRequestAdd2Amount = totalRequestAdd2Amount;
        copyLine.totalRequestDiscount = totalRequestDiscount;
        copyLine.totalRequestTax = totalRequestTax;
        copyLine.totalRequestCharge = totalRequestCharge;
        copyLine.totalRequestNetAmount = totalRequestNetAmount;
        copyLine.sortId = sortId;
        copyLine.indexInfo = indexInfo;
        copyLine.callUniqueId = callUniqueId;
        copyLine.weight = weight;
        copyLine.priceId = priceId;
        copyLine.unitPrice = unitPrice;
        copyLine.comment = comment;
        copyLine.cartonType = cartonType;
        copyLine.userprice = userprice;
        copyLine.evcId = evcId;
        copyLine.volume = volume;
        copyLine.freeReasonId = freeReasonId;

        copyLine.invoiceTotalQty = requestTotalQty;
        copyLine.invoiceBigQty = requestBigQty;
        copyLine.invoiceBigQtyId = requestBigQtyId;
        copyLine.invoiceBigQtyName = requestBigQtyName;
        copyLine.invoiceSmallQty = requestSmallQty;
        copyLine.invoiceSmallQtyId = requestSmallQtyId;
        copyLine.invoiceSmallQtyName = requestSmallQtyName;
        copyLine.totalInvoiceAmount = totalRequestAmount;
        copyLine.totalInvoiceDiscount = totalRequestDiscount;
        copyLine.totalInvoiceTax = totalRequestTax;
        copyLine.totalInvoiceCharge = totalRequestCharge;
        copyLine.totalInvoiceNetAmount = totalRequestNetAmount;
        copyLine.totalInvoiceAdd1Amount = totalRequestAdd1Amount;
        copyLine.totalInvoiceAdd2Amount = totalRequestAdd2Amount;


        copyLine.invoiceDis1 = totalRequestDis1Amount;
        copyLine.invoiceDis2 = totalRequestDis2Amount;
        copyLine.invoiceDis3 = totalRequestDis3Amount;

        return copyLine;
    }

    //endregion Action methods

    //region Cast method
    public DiscountCallOrderLineDataOnline ToOnline() {
        DiscountCallOrderLineDataOnline onlineData = new DiscountCallOrderLineDataOnline();
        onlineData.TotalQty = this.invoiceBigQty;
        onlineData.GoodsRef = String.valueOf(this.productId);
        onlineData.FreeReasonRef = (this.freeReasonId == 0) ? null : this.freeReasonId;
        onlineData.SaleNo = saleNo;
        onlineData.ReferenceNo = referenceNo;
        onlineData.ReturnReasonId = returnReasonId;
        onlineData.saleSITEM = saleS_ITEM;
        onlineData.OrderDate = this.orderDate;
        onlineData.OrderLineId = this.orderLineId;
        onlineData.OrderId = this.orderUniqueId;
        return onlineData;
    }

    @SuppressLint("SuspiciousIndentation")
    public void SetFromOnline(OutputOnlineDetails onlinedata, ArrayList<DiscountCallOrderLineData> baseDatas) {

        this.evcId = UUID.randomUUID().toString();/*onlinedata.evcRef.toString();*/
        this.productId = onlinedata.goodsRef;
        this.disRef = onlinedata.disRef == null ? 0 : onlinedata.disRef;
        this.invoiceTotalQty = onlinedata.totalQty;
        this.userprice = onlinedata.userPrice;
        this.PayDuration = onlinedata.payDuration;
        this.RuleNo = onlinedata.ruleNo;
/*
        onlinedata.RowOrder ;
        onlinedata.PrizeType ;
        onlinedata.UnitQty ;
         onlinedata.CPriceRef;
        onlinedata.AccYear ;
        onlinedata.UnitRef ;
        onlinedata.UnitCapasity ;
        onlinedata.CustPrice ;
        onlinedata.SupAmount ;
        onlinedata.AddAmount ;
        onlinedata.PeriodicDiscountRef ;
*/

        this.totalInvoiceNetAmount = onlinedata.amountNut;
        this.freeReasonId = onlinedata.freeReasonId == null ? 0 : onlinedata.freeReasonId;
        this.invoiceDis1 = onlinedata.evcItemDis1;
        this.invoiceDis2 = onlinedata.evcItemDis2;
        this.invoiceDis3 = onlinedata.evcItemDis3;
        this.TakhfifatKol= onlinedata.takhfifatkol;
        this.evcItemAdd= onlinedata.evcItemAdd1;
        this.AmountNutPT03= onlinedata.amountNutPT03;
        this.Fee= onlinedata.fee;
        this.FeeKol= onlinedata.feeKol;

        this.totalQty= onlinedata.totalQty;
        this.zterm= onlinedata.zterm;
        this.totalInvoiceAdd1Amount = onlinedata.evcItemAdd1;
        this.evcItemAdd = onlinedata.evcItemAdd1;
        this.totalInvoiceAdd2Amount = onlinedata.evcItemAdd2;
        this.cart = onlinedata.cart;
        this.saleS_ITEM = onlinedata.saleS_ITEM;

        this.invoiceBigQty = onlinedata.unitQty;
//        this.invoiceBigQtyId = onlinedata.unitRef;
//        this.invoiceBigQtyName = onlinedata.;
        this.unitCapacity = onlinedata.unitCapasity != null ? onlinedata.unitCapasity : 1;
        this.isRequestPrizeItem = onlinedata.prizeType != null && (onlinedata.prizeType != 0);
        if (onlinedata.orderLineId != null)
            this.orderLineId = onlinedata.orderLineId;

        DiscountCallOrderLineData baseData = null;
        for (DiscountCallOrderLineData lineData : baseDatas) {
            if (this.orderLineId != null && lineData.orderLineId.equals(this.orderLineId)) {
                baseData = lineData;
                break;
            } else if (this.orderLineId == null && lineData.productId == this.productId && lineData.isRequestPrizeItem == this.isRequestPrizeItem && lineData.freeReasonId == this.freeReasonId) {
                baseData = lineData;
                break;
            }
        }
        /**
         * ThirdParty or zar
         * ThirdParty محصوص زر ماکارون
         */
        if (GlobalVariables.getIsThirdParty() && VaranegarApplication.is(VaranegarApplication.AppId.Dist) && (onlinedata.disRef == null || onlinedata.disRef == 0) && baseData != null) {
            this.unitPrice = baseData.unitPrice;

            this.amount = onlinedata.amount;
            this.custPrice= onlinedata.custPrice;
            this.totalInvoiceAmount = unitPrice.multiply(invoiceTotalQty);

            if (amount!=null)
                this.totalInvoiceDiscount = (baseData.unitPrice.multiply(invoiceTotalQty)).subtract(onlinedata.amount);
//      this.totalInvoiceAmount = unitPrice.multiply(invoiceTotalQty);


//            this.totalInvoiceDiscount = onlinedata.takhfifatkol;
        } else {
            this.unitPrice = onlinedata.custPrice;
            this.totalInvoiceAmount = onlinedata.amount;
            this.totalInvoiceDiscount = onlinedata.discount;
        }

        if (GlobalVariables.getIsThirdParty()) {
            long bigQtyId = baseData != null ? baseData.requestBigQtyId : 0;
            this.invoiceBigQtyId = onlinedata.unitRef != null ? onlinedata.unitRef : bigQtyId;

        } else {
            this.invoiceBigQtyId = onlinedata.unitRef;
        }

        this.orderLineId = baseData != null ? baseData.orderLineId : null;
        this.requestBigQty = baseData != null ? baseData.requestBigQty : BigDecimal.ZERO;
        this.requestBigQtyId = baseData != null ? baseData.requestBigQtyId : this.invoiceBigQtyId;
        this.requestBigQtyName = baseData != null ? baseData.requestBigQtyName : this.invoiceBigQtyName;


        this.requestSmallQty = baseData != null ? baseData.requestSmallQty : BigDecimal.ZERO;
        this.requestSmallQtyId = baseData != null ? baseData.requestSmallQtyId : this.invoiceSmallQtyId;
        this.requestSmallQtyName = baseData != null ? baseData.requestSmallQtyName : this.invoiceSmallQtyName;
        this.requestTotalQty = baseData != null ? baseData.requestTotalQty : BigDecimal.ZERO;
        this.totalRequestAdd1Amount = baseData != null ? baseData.totalRequestAdd1Amount : BigDecimal.ZERO;
        this.totalRequestAdd2Amount = baseData != null ? baseData.totalRequestAdd2Amount : BigDecimal.ZERO;
        this.totalRequestAmount = baseData != null ? baseData.totalRequestAmount : BigDecimal.ZERO;
        this.totalRequestCharge = baseData != null ? baseData.totalRequestCharge : BigDecimal.ZERO;
        this.totalRequestDiscount = baseData != null ? baseData.totalRequestCharge : BigDecimal.ZERO;
        this.totalRequestTax = baseData != null ? baseData.totalRequestTax : BigDecimal.ZERO;
        this.totalRequestNetAmount = baseData != null ? baseData.totalRequestNetAmount : BigDecimal.ZERO;
//        this.unitPrice = baseData != null ? baseData.unitPrice : BigDecimal.ZERO;
        this.userprice = baseData != null ? baseData.userprice : BigDecimal.ZERO;
        this.priceId = baseData != null ? baseData.priceId : 0l;

        DiscountProduct product = DiscountProductDBAdapter.getInstance().getProductById(this.productId);
        this.productName = product != null ? product.productName : null;
        this.productCode = product != null ? product.productCode : null;

        this.invoiceDisOther = onlinedata.evcItemDisOther != null ? onlinedata.evcItemDisOther : BigDecimal.ZERO;
        this.totalInvoiceAddOther = onlinedata.evcItemAddOther != null ? onlinedata.evcItemAddOther : BigDecimal.ZERO;
        this.adjustmentPrice = onlinedata.adjustmentPrice;

        this.amountCash = onlinedata.amountCash;
        this.amountCheque = onlinedata.amountCheque;
        this.amountNutCash = onlinedata.amountNutCash;
        this.amountNutCheque = onlinedata.amountNutCheque;
        this.amountImmediate = onlinedata.amountImmediate;
        this.amountNutImmediate = onlinedata.amountNutImmediate;

        this.chequeDiscount =onlinedata.chequeDiscount;
        this.cashDiscount=onlinedata.cashDiscount;

    }

}
