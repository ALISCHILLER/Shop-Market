package varanegar.com.discountcalculatorlib.viewmodel;


import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.general.DiscountFreeReasonDBAdapter;
import varanegar.com.discountcalculatorlib.entity.general.DiscountFreeReason;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.OrderTypeEnum;
import varanegar.com.discountcalculatorlib.utility.enumerations.OrderVisitStatus;
import varanegar.com.vdmclient.call.CalcData;
import varanegar.com.vdmclient.call.Customer;
import varanegar.com.vdmclient.call.EvcHeader;
import varanegar.com.vdmclient.call.EvcItem;

public class DiscountCallOrderData {

    public String evcRef;
    public int customerId;
    public String orderUniqueId;
    public int saleId;
    public String saleNo;
    public String saleDate;
    public int orderId;
    public int orderNo;
    public int distId;
    public String callUniqueId;
    public BigDecimal totalOrderAmount;
    public BigDecimal totalOrderTax;
    public BigDecimal totalOrderDiscount;
    public BigDecimal totalOrderCharge;
    public BigDecimal totalOrderNetAmount;
    public BigDecimal totalInvoiceAmount;
    public BigDecimal totalInvoiceDiscount;
    public BigDecimal totalInvoiceAdd;
    public BigDecimal totalInvoiceTax;
    public BigDecimal totalInvoiceCharge;
    public BigDecimal totalInvoiceDis1;
    public BigDecimal totalInvoiceDis2;
    public BigDecimal totalInvoiceDis3;
    public BigDecimal totalInvoiceDisOther;
    public BigDecimal totalInvoiceAdd1;
    public BigDecimal totalInvoiceAdd2;
    public BigDecimal totalInvoiceAddOther;
    public BigDecimal totalInvoiceNetAmount;
    public int orderVisitStatusId;
    public String orderVisitStatusText;
    public String orderRejectReason;
    public int orderRejectReasonId;
    public int localPaperNo;
    public String Comment;
    public OrderTypeEnum orderType;
    public String orderPaymentTypeId;
    public String orderPaymentTypeName;
    public UUID orderPaymentTypeUniqueId;
    public String orderPaymentType;
    public String invoicePaymentTypeId;
    public String invoicePaymentType;
    public int disTypeId;
    public int OrderType;

    public BigDecimal totalOrderAdd1;
    public BigDecimal totalOrderAdd2;
    public BigDecimal totalOrderDis1;
    public BigDecimal totalOrderDis2;
    public BigDecimal totalOrderDis3;

    public BigDecimal orderManualDiscont;
    public BigDecimal orderFreight;

    public BigDecimal invoiceManualDiscont;
    public BigDecimal invoiceFreight;

    public int DCRef;
    public int SaleOfficeRef;

    public String dcCode;
    public int accYear;
    public int stockDCRef;
    public String stockDCCode;
    public int dealerRef;
    public String dealerCode;
    public int supervisorRef;
    public String supervisorCode;
    public Context con;

    public String dealerName;
    public String supervisorName;
    public String stockDCName;

    // new
    public int DcSaleOfficeRef;
    public int TOrderRef;
    public int TOrderNo;
    public int BuyTypeRef;
    public int UsanceDay;
    public int OrderRef;
    public String DCName;
    public String PayTypeName;
    public String DisTypeName;
    public String OrderTypeName;
    public String SaleOfficeName;
    public String PaymentUsanceRef;
    public String PaymentUsanceName;
    public double CashDuration;
    public double CheckDuration;

    public ArrayList<DiscountEvcItemStatuteData> callOrderEvcItemStatutes;
    public ArrayList<DiscountCallOrderLineData> callOrderLineItemData;
    public ArrayList<DiscountCallOrderLineData> callLineItemDataWithPromo;
    public ArrayList<DiscountCallOrderLineData> callLineItemDataWithPromoBeforeChange;
    public ArrayList<DiscountEvcPrizeData> discountEvcPrize;

    public ArrayList<CallOrderLineGoodPackageItemData> callOrderLineGoodPackageItemDatas;


    public DiscountCallOrderData() {

    }

    public DiscountCallOrderData(String callUniqueId, int saleId, String orderUniqueId, String saleDate, String saleNo
            , int distId, BigDecimal totalOrderAmount, BigDecimal totalOrderTax, BigDecimal totalOrderDiscount
            , BigDecimal totalOrderCharge, BigDecimal totalOrderNetAmount, BigDecimal totalInvoiceAmount
            , BigDecimal totalInvoiceDiscount, BigDecimal totalInvoiceTax, BigDecimal totalInvoiceCharge
            , BigDecimal totalInvoiceDis1, BigDecimal totalInvoiceDis2, BigDecimal totalInvoiceDis3
            , BigDecimal totalInvoiceAdd1, BigDecimal totalInvoiceAdd2, BigDecimal totalInvoiceNetAmount
            , int orderVisitStatusId, String orderVisitStatusText, String orderRejectReason, int orderRejectReasonId, int localPaperNo, int customerId
            , String Comment, int orderTypeId, String orderPaymentTypeId, String orderPaymentType, String invoicePaymentTypeId, String invoicePaymentType
            , int disTypeId, BigDecimal totalOrderDis1, BigDecimal totalOrderDis2, BigDecimal totalOrderDis3
            , int DCRef, BigDecimal totalOrderAdd1, BigDecimal totalOrderAdd2, int SaleOfficeRef
            , String dcCode, int accYear, int stockDCRef, String stockDCCode, int dealerRef, String dealerCode, int supervisorRef, String supervisorCode
            , int orderId, int orderNo, int OrderType
            , BigDecimal orderManualDiscont, BigDecimal orderFreight
            , BigDecimal invoicManualDiscont, BigDecimal invoicFreight
            , BigDecimal totalInvoiceDisOther
            , BigDecimal totalInvoiceAddOther

    ) {
        this.callUniqueId = callUniqueId;
        this.saleId = saleId;
        this.orderUniqueId = orderUniqueId;
        this.saleDate = saleDate;
        this.saleNo = saleNo;
        this.distId = distId;
        this.totalOrderAmount = totalOrderAmount;
        this.totalOrderTax = totalOrderTax;
        this.totalOrderDiscount = totalOrderDiscount;
        this.totalOrderCharge = totalOrderCharge;
        this.totalOrderNetAmount = totalOrderNetAmount;
        this.totalInvoiceAmount = totalInvoiceAmount;
        this.totalInvoiceDiscount = totalInvoiceDiscount;
        this.totalInvoiceTax = totalInvoiceTax;
        this.totalInvoiceCharge = totalInvoiceCharge;
        this.totalInvoiceNetAmount = totalInvoiceNetAmount;
        this.orderVisitStatusId = orderVisitStatusId;
        this.orderVisitStatusText = orderVisitStatusText;
        this.orderRejectReason = orderRejectReason;
        this.orderRejectReasonId = orderRejectReasonId;
        this.localPaperNo = localPaperNo;
        this.customerId = customerId;
        this.orderType = OrderTypeEnum.getOrderType(orderTypeId);
        this.Comment = Comment;
        this.orderPaymentTypeId = orderPaymentTypeId;
        this.orderPaymentType = orderPaymentType;
        this.invoicePaymentTypeId = invoicePaymentTypeId;
        this.invoicePaymentType = invoicePaymentType;
        this.disTypeId = disTypeId;
        this.OrderType = OrderType;

        this.totalInvoiceDis1 = totalInvoiceDis1;
        this.totalInvoiceDis2 = totalInvoiceDis2;
        this.totalInvoiceDis3 = totalInvoiceDis3;
        this.totalInvoiceDisOther = totalInvoiceDisOther;
        this.totalOrderDis1 = totalOrderDis1;
        this.totalOrderDis2 = totalOrderDis2;
        this.totalOrderDis3 = totalOrderDis3;
        this.DCRef = DCRef;
        this.totalOrderAdd1 = totalOrderAdd1;
        this.totalOrderAdd2 = totalOrderAdd2;
        this.totalInvoiceAdd1 = totalInvoiceAdd1;
        this.totalInvoiceAdd2 = totalInvoiceAdd2;
        this.totalInvoiceAddOther = totalInvoiceAddOther;
        this.SaleOfficeRef = SaleOfficeRef;

        this.dcCode = dcCode;
        this.accYear = accYear;
        this.stockDCRef = stockDCRef;
        this.stockDCCode = stockDCCode;
        this.dealerRef = dealerRef;
        this.dealerCode = dealerCode;
        this.supervisorRef = supervisorRef;
        this.supervisorCode = supervisorCode;
        this.orderId = orderId;
        this.orderNo = orderNo;

        this.orderFreight = orderFreight;
        this.orderManualDiscont = orderManualDiscont;
        this.invoiceFreight = invoicFreight;
        this.invoiceManualDiscont = invoicManualDiscont;
    }

    //region Validation methods

    public boolean isConfirmed() {
        if (orderVisitStatusId == OrderVisitStatus.UNKNOWN.value())
            return false;
        else
            return true;
    }

    public boolean isEdited() {
        boolean result = false;
        for (DiscountCallOrderLineData lineData : callOrderLineItemData) {
            if (lineData.requestTotalQty.compareTo(lineData.invoiceTotalQty) != 0)
                result = true;
        }
        return result;
    }

    public boolean hasRejected() {
        if (orderVisitStatusId == OrderVisitStatus.NOT_DELIVERED.value() ||
                orderVisitStatusId == OrderVisitStatus.PARTIAL_DELIVERED.value())
            return true;
        else
            return false;
    }

    //endregion Validation methods

    //region Calculation methods

    public OrderVisitStatus getOrderVisitStatus() {
        if (isEdited()) {
            boolean rejected = true;
            for (DiscountCallOrderLineData lineData : callOrderLineItemData) {
                if (lineData.invoiceTotalQty.compareTo(BigDecimal.ZERO) != 0) {
                    rejected = false;
                    break;
                }
            }

            if (rejected)
                return OrderVisitStatus.NOT_DELIVERED;
            else
                return OrderVisitStatus.PARTIAL_DELIVERED;
        } else
            return OrderVisitStatus.DELIVERED;
    }

    private void CalcValues() {

        BigDecimal sumAmount, sumDiscount, sumTax, sumCharge, sumAdd1, sumAdd2, sumNetAmount;
        sumAmount = sumDiscount = sumTax = sumCharge = sumAdd1 = sumAdd2 = sumNetAmount = BigDecimal.ZERO;

        for (DiscountCallOrderLineData lineData : callLineItemDataWithPromo) {
            sumAmount = sumAmount.add(lineData.totalInvoiceAmount);
            sumDiscount = sumDiscount.add(lineData.totalInvoiceDiscount);
            sumTax = sumTax.add(lineData.totalInvoiceTax);
            sumCharge = sumCharge.add(lineData.totalInvoiceCharge);
            sumAdd1 = sumAdd1.add(lineData.totalInvoiceAdd1Amount);
            sumAdd2 = sumAdd2.add(lineData.totalInvoiceAdd2Amount);
            sumNetAmount = sumNetAmount.add(lineData.totalInvoiceNetAmount);
        }
        //this.invoicePaymentTypeId = this.orderPaymentTypeId;
        //this.invoicePaymentType = this.orderPaymentType;
        this.totalInvoiceAmount = sumAmount;
        this.totalInvoiceDiscount = sumDiscount;
        this.totalInvoiceTax = sumTax;
        this.totalInvoiceCharge = sumCharge;
        this.totalInvoiceAdd1 = sumAdd1;
        this.totalInvoiceAdd2 = sumAdd2;
        this.totalInvoiceNetAmount = sumNetAmount;
    }

    private void copyLinesForFreeInvoice(OrderVisitStatus status) {
        this.callLineItemDataWithPromo.clear();
        for (DiscountCallOrderLineData lineData : this.callOrderLineItemData) {
            DiscountCallOrderLineData newLine = lineData;


            if (lineData.isFreeItem == 0) {
                lineData.totalInvoiceAmount = lineData.invoiceTotalQty.multiply(lineData.unitPrice).setScale(0, BigDecimal.ROUND_UP);

                lineData.totalInvoiceNetAmount = lineData.totalInvoiceAmount.subtract(lineData.totalInvoiceDiscount).add(lineData.totalInvoiceAdd1Amount).add(lineData.totalInvoiceAdd2Amount);

                BigDecimal invnet = lineData.totalInvoiceNetAmount.add(lineData.totalRequestTax).add(lineData.totalRequestCharge);
                //DiscountProductTaxInfo taxInfo = DiscountProductTaxInfoDBAdapter.getInstance().getProductTaxInfo(lineData.productId);
                if (invnet.compareTo(lineData.totalRequestNetAmount) == 0) {
                    lineData.totalInvoiceTax = lineData.totalRequestTax;
                    lineData.totalInvoiceCharge = lineData.totalRequestCharge;
                } else if (lineData.totalRequestNetAmount.compareTo(BigDecimal.ZERO) == 1 &&
                        lineData.totalInvoiceNetAmount.compareTo(BigDecimal.ZERO) == 1) {
                    //BigDecimal tax = lineData.totalInvoiceTax.add(lineData.totalInvoiceNetAmount.multiply(taxInfo.taxPercent).divide(new BigDecimal(100), BigDecimal.ROUND_UP));
                    //BigDecimal charge = lineData.totalInvoiceCharge.add(lineData.totalInvoiceNetAmount.multiply(taxInfo.chargePerent).divide(new BigDecimal(100), BigDecimal.ROUND_UP));

                    BigDecimal tax = BigDecimal.ZERO;
                    BigDecimal charge = BigDecimal.ZERO;
                    if (lineData.totalRequestTax.compareTo(BigDecimal.ZERO) == 1)
                        tax = lineData.totalRequestTax.divide(lineData.totalRequestNetAmount, 20, RoundingMode.HALF_UP)
                                .multiply(lineData.totalInvoiceNetAmount).setScale(0, RoundingMode.HALF_UP);
                    if (lineData.totalRequestCharge.compareTo(BigDecimal.ZERO) == 1)
                        charge = lineData.totalRequestCharge.divide(lineData.totalRequestNetAmount, 20, RoundingMode.HALF_UP)
                                .multiply(lineData.totalInvoiceNetAmount).setScale(0, RoundingMode.HALF_UP);

                    lineData.totalInvoiceTax = tax;
                    lineData.totalInvoiceCharge = charge;
                } else {
                    lineData.totalInvoiceTax = BigDecimal.ZERO;
                    lineData.totalInvoiceCharge = BigDecimal.ZERO;
                }
                lineData.totalInvoiceNetAmount = lineData.totalInvoiceAmount.add(lineData.totalInvoiceCharge)
                        .add(lineData.totalInvoiceAdd1Amount).add(lineData.totalInvoiceAdd2Amount)
                        .add(lineData.totalInvoiceTax).subtract(lineData.totalInvoiceDiscount);

            }
            this.callLineItemDataWithPromo.add(newLine);
        }
    }

    private void copyLines(OrderVisitStatus status) {
        this.callLineItemDataWithPromo.clear();

        if (status == OrderVisitStatus.DELIVERED) {
            for (DiscountCallOrderLineData lineData : this.callOrderLineItemData) {
                DiscountCallOrderLineData newLine = lineData.copyForWithPromo();
                this.callLineItemDataWithPromo.add(newLine);
            }
        } else {
            this.callLineItemDataWithPromo.addAll(this.callOrderLineItemData);
        }
    }

    public PreSaleEvcHeaderViewModel toOnlineDist(List<DiscountOrderPrizeViewModel> OrderPrizeList) {
        PreSaleEvcHeaderViewModel onlineData = new PreSaleEvcHeaderViewModel();
        onlineData.OrderRef = this.OrderRef;
        onlineData.BuyTypeRef = this.BuyTypeRef;//TODO: ASAL
        onlineData.CustRef = String.valueOf(this.customerId);
        onlineData.OrderTypeRef = this.OrderType;
        onlineData.SaleOfficeRef = this.SaleOfficeRef;
        onlineData.OrderDate = "";//TODO: ASAL
        onlineData.DealerRef = this.dealerRef;
        onlineData.DisType = this.disTypeId;
        onlineData.PaymentUsanceRef = this.invoicePaymentTypeId; //TODO: ASAL
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            onlineData.EvcType = 3;
        else
            onlineData.EvcType = 1;

        onlineData.RefId = this.orderId;

        DiscountCallOrderLineDataOnline itemOnline;
        onlineData.PreSaleEvcDetails = new ArrayList<>();
        for (DiscountCallOrderLineData item : this.callOrderLineItemData) {
            itemOnline = item.ToOnline();
            onlineData.PreSaleEvcDetails.add(itemOnline);
        }
        onlineData.OrderPrizeList = OrderPrizeList;
        return onlineData;
    }

    public DiscountCallOrderDataOnline ToOnline() {
        DiscountCallOrderDataOnline onlineData = new DiscountCallOrderDataOnline();
        onlineData.BuyTypeRef = this.BuyTypeRef;//TODO: ASAL
        onlineData.CustRef = String.valueOf(this.customerId);
        onlineData.OrderTypeRef = this.OrderType;
        onlineData.SaleOfficeRef = this.SaleOfficeRef;
        onlineData.OrderDate = "";//TODO: ASAL
        onlineData.DealerRef = this.dealerRef;
        onlineData.DisType = this.disTypeId;
        onlineData.PaymentUsanceRef = this.invoicePaymentTypeId; //TODO: ASAL
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            onlineData.EvcType = 3;
        else
            onlineData.EvcType = 1;

        onlineData.RefId = this.orderId;

        DiscountCallOrderLineDataOnline itemOnline;
        onlineData.PreSaleEvcDetails = new ArrayList<>();


        for (DiscountCallOrderLineData item : this.callOrderLineItemData) {
            itemOnline = item.ToOnline();
            onlineData.PreSaleEvcDetails.add(itemOnline);
        }
        return onlineData;
    }

    /**
     * convert data online for discoundCallorder
     * مبدل دیتای انلاین به مدل داخلی
     * پیش نمایش تخفیف جوایز
     * @param onlineDatas
     * @param baseDatas
     */

    public void SetFromOnline(DiscountOutputOnline onlineDatas, ArrayList<DiscountCallOrderLineData> baseDatas) {
        this.callLineItemDataWithPromo = new ArrayList<>();
        HashMap<Integer, UUID> orderLinesId = new HashMap<>();
        for (OutputOnlineDetails onlineData : onlineDatas.items) {
            DiscountCallOrderLineData offlineData = new DiscountCallOrderLineData();
            offlineData.SetFromOnline(onlineData, baseDatas);
            this.orderPaymentTypeUniqueId = onlineDatas.paymentUsanceId;
            this.orderPaymentTypeId = onlineDatas.paymentUsanceRef;
            this.orderPaymentTypeName = onlineDatas.paymentUsanceName;
            this.callLineItemDataWithPromo.add(offlineData);
            orderLinesId.put(onlineData.id, offlineData.orderLineId);
        }
        this.discountEvcPrize = onlineDatas.discountEvcPrize;
        ArrayList<DiscountEvcItemStatuteData> statutes = new ArrayList<>();
        if (onlineDatas.itemStatute != null) {
            for (DiscountEvcItemStatuteDataOnline item : onlineDatas.itemStatute) {
                DiscountEvcItemStatuteData discountEvcItemStatuteData = new DiscountEvcItemStatuteData();
                discountEvcItemStatuteData.OrderLineId = (orderLinesId.get(item.evcItemRef) != null) ? (orderLinesId.get(item.evcItemRef).toString()) : "";
                discountEvcItemStatuteData.EvcItemRef = item.evcItemRef;
                discountEvcItemStatuteData.DisRef = item.disRef;
                discountEvcItemStatuteData.Discount = (long) item.discount;
                discountEvcItemStatuteData.AddAmount = (long) item.addAmount;
                discountEvcItemStatuteData.SupAmount = (long) item.supAmount;
                statutes.add(discountEvcItemStatuteData);
            }
        }
        this.callOrderEvcItemStatutes = statutes;
        this.UsanceDay = onlineDatas.usanceDay;
        this.PaymentUsanceRef = onlineDatas.paymentUsanceRef;
        this.CheckDuration = onlineDatas.checkDuration;
        this.CashDuration = onlineDatas.cashDuration;
    }

    public static DiscountCallOrderData fromCalcData(CalcData calcData) {
        DiscountCallOrderData discountData = new DiscountCallOrderData();

        if (calcData.EvcHeaders == null || calcData.EvcHeaders.isEmpty())
            return discountData;

        EvcHeader header = calcData.EvcHeaders.get(0);
        discountData.customerId = header.CustRef;
        discountData.totalInvoiceAmount = header.TotalAmount;
        header.RefId = 0;
        discountData.disTypeId = header.DiscountTypeValue;
        discountData.DCRef = header.DcRef;
        discountData.DcSaleOfficeRef = header.DcSaleOfficeRef;
        discountData.SaleOfficeRef = header.SaleOfficeRef;
        discountData.dcCode = header.DcCode;
        discountData.saleDate = header.DateOf;
        discountData.saleDate = header.OprDate;
        discountData.saleDate = header.EvcDate;
        discountData.accYear = header.AccYear;
        discountData.stockDCRef = header.StockDcRef;
        discountData.stockDCCode = header.StockDcCode;
        discountData.dealerRef = header.DealerRef;
        discountData.dealerCode = header.DealerCode;
        discountData.supervisorRef = header.SupervisorRef;
        discountData.supervisorCode = header.SupervisorCode;
        discountData.stockDCName = header.StockDCName;
        discountData.supervisorName = header.SupervisorName;
        discountData.saleId = header.SaleRef;
        discountData.orderNo = header.OrderNo;
//new
        discountData.PaymentUsanceRef = header.PaymentUsanceRef;
        discountData.PaymentUsanceName = header.PaymentUsanceName;
        discountData.TOrderRef = header.TOrderRef;
        discountData.TOrderNo = header.TOrderNo;
        discountData.BuyTypeRef = header.BuyTypeRef;
        discountData.UsanceDay = header.UsanceDay;
        discountData.OrderRef = header.OrderRef;
        discountData.DCName = header.DCName;
        discountData.PayTypeName = header.PayTypeName;
        discountData.DisTypeName = header.DisTypeName;
        discountData.OrderTypeName = header.OrderTypeName;
        discountData.SaleOfficeName = header.SaleOfficeName;
        discountData.OrderType = header.OrderType;
        //discountData. = header.PayType;
        discountData.callUniqueId = header.CallId;

        discountData.callLineItemDataWithPromo = new ArrayList<>();
        BigDecimal dis1 = BigDecimal.ZERO, dis2 = BigDecimal.ZERO,
                dis3 = BigDecimal.ZERO, disOther = BigDecimal.ZERO,
                add1 = BigDecimal.ZERO, add2 = BigDecimal.ZERO,
                addOther = BigDecimal.ZERO, tax = BigDecimal.ZERO, charge = BigDecimal.ZERO;

        DiscountCallOrderLineData line;
        for (EvcItem item : calcData.EvcItems) {
            line = fillDiscountItem(item);
            discountData.callLineItemDataWithPromo.add(line);
            dis1.add(line.invoiceDis1);
            dis2.add(line.invoiceDis2);
            dis3.add(line.invoiceDis3);
            add1.add(line.totalInvoiceAdd1Amount);
            add2.add(line.totalInvoiceAdd2Amount);
            tax.add(line.totalInvoiceTax);
            charge.add(line.totalInvoiceCharge);
        }
        discountData.totalInvoiceDis1 = dis1;
        discountData.totalInvoiceDis2 = dis2;
        discountData.totalInvoiceDis3 = dis3;
        discountData.totalInvoiceDisOther = disOther;
        discountData.totalInvoiceDiscount = dis1.add(dis2.add(dis3.add(disOther)));

        discountData.totalInvoiceAdd1 = add1;
        discountData.totalInvoiceAdd2 = add2;
        discountData.totalInvoiceAddOther = addOther;
        discountData.totalInvoiceTax = tax;
        discountData.totalInvoiceCharge = charge;
        discountData.totalInvoiceAdd = add1.add(add2.add(addOther));

        return discountData;
    }

    private static DiscountCallOrderLineData fillDiscountItem(EvcItem item) {
        int calcType = 0;
        DiscountCallOrderLineData line = new DiscountCallOrderLineData();

        if (line.freeReasonId != 0) {
            DiscountFreeReason discountFreeReason = DiscountFreeReasonDBAdapter.getInstance().getFreeReasonById(line.freeReasonId);
            if (discountFreeReason != null)
                calcType = discountFreeReason.calcPriceType;
        }


        line.sortId = item.RowOrder;
        line.productId = item.GoodsRef;
        line.GoodsCtgrRef = item.GoodsCtgrRef;
        line.invoiceTotalQty = new BigDecimal(item.TotalQty);
        line.disRef = item.DisRef;
        if (item.TotalWeight != null)
            line.weight = new BigDecimal(item.TotalWeight);

        line.unitPrice = item.CustPrice;
        line.userprice = item.UserPrice;
        item.PrizeType = 0;
        line.priceId = item.CPriceRef;
        line.priceId = item.PriceRef;
        line.invoiceBigQty = item.UnitQty;
        line.invoiceBigQtyId = item.UnitRef;
        line.evcId = item.EvdId;
        line.callUniqueId = item.CallId;
        line.freeReasonId = item.FreeReasonId;
        line.unitPrice = item.SalePrice;
        line.invoiceTotalQty = item.Amount;
        line.totalInvoiceAmount = item.Amount;
        line.totalInvoiceDiscount = item.Discount;
        //item.ChargePercent;
        //item.TaxPercent;
        //item.SupAmount;
        //item.AddAmount;
        //item.PeriodicDiscountRef = 0;
        line.totalInvoiceTax = item.Tax;
        line.totalInvoiceCharge = item.Charge;
        line.invoiceDis1 = item.EvcItemDis1;
        line.invoiceDis2 = item.EvcItemDis2;
        line.invoiceDis3 = item.EvcItemDis3;
        line.totalInvoiceAdd1Amount = item.EvcItemAdd1;
        line.totalInvoiceAdd2Amount = item.EvcItemAdd2;
        line.totalInvoiceNetAmount = item.Amount.add(item.AddAmount).subtract(item.Discount);
        //item.EvcItemOtherDiscount;
        //item.EvcItemOtherAddition;
        return line;
    }

    public static CalcData toCalcData(DiscountCallOrderData discountData, int evcType) {
        CalcData calcData = new CalcData();
        calcData.Customers = new ArrayList<>();
        Customer customer = new Customer();
        customer = DiscountCustomerDBAdapter.getInstance().getById(discountData.customerId);
        calcData.Customers.add(customer);
        calcData.EvcHeaders = fillEVCHeader(discountData, evcType);
        calcData.EvcItems = fillEVCItem(discountData);

        return calcData;
    }

    private static List<EvcHeader> fillEVCHeader(DiscountCallOrderData discountData, int evcType) {
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
        header.OprDate = discountData.saleDate;
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
        header.OrderRef = discountData.OrderRef;
        header.DCName = discountData.DCName;
        header.PayTypeName = discountData.PayTypeName;
        header.DisTypeName = discountData.DisTypeName;
        header.OrderTypeName = discountData.OrderTypeName;
        header.SaleOfficeName = discountData.SaleOfficeName;

        headers.add(header);
        return headers;


    }

    private static List<EvcItem> fillEVCItem(DiscountCallOrderData discountData) {
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
            item.UnitCapasity = 1;
            if (line.weight != null)
                item.TotalWeight = line.weight.longValue();


            if (calcType == 2) {
                item.Amount = BigDecimal.ZERO;
                item.CustPrice = BigDecimal.ZERO;
                item.UserPrice = BigDecimal.ZERO;
                //item.AmountNut = BigDecimal.ZERO;
            } else {
                if (GlobalVariables.getDecimalDigits() > 0) {
                    BigDecimal cartonType = new BigDecimal(line.cartonType * 1.0);
                    //item.AmountNut =  Math.round(Math.round((unitPrice.doubleValue() * cartonType) * 1.0) * (totalQty.longValue() / (cartonType * 1.0))));
                    item.Amount = new BigDecimal(Math.round(Math.round(line.unitPrice.multiply(cartonType).doubleValue())
                            * (line.invoiceTotalQty.divide(cartonType)).doubleValue()));
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
            item.EvcRef = i;
            item.CallId = line.callUniqueId;
            item.Volume = 0;
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
        return items;
    }

}
