package com.varanegar.vaslibrary.promotion;

import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderLineGoodPackage.CustomerCallOrderLineGoodPackage;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.ApplicationType;
import varanegar.com.discountcalculatorlib.utility.enumerations.OrderTypeEnum;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountEvcItemStatuteData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountEvcPrizeData;

import static com.varanegar.vaslibrary.base.LocalModel.English;

/**
 * Created by Asal on 11/07/2017.
 */

public class CustomerCallOrderPromotion {
    public UUID UniqueId;

    public UUID CustomerId;
    public int CustomerRef;
    public String CustomerName;
    public String CustomerCode;
    public Currency RemainDebit;
    public Currency RemainCredit;

    public CustomerModel customer;

    public UUID OrderPaymentTypeId;
    public String OrderPaymentTypeRef;
    public String OrderPaymentTypeName;

    public UUID OrderTypeId;
    public int OrderTypeRef;
    public String OrderTypeName;


    public int DistBackOfficeId;
    public int DisType;
    public String Comment;
    public int LocalPaperNo;
    public String BackOfficeOrderNo;
    public Date SaleDate;
    public int BackOfficeOrderId;
    public Currency RoundOrderAmount;
    public Currency RoundOrderOtherDiscount;
    public Currency RoundOrderDis1;
    public Currency RoundOrderDis2;
    public Currency RoundOrderDis3;
    public Currency RoundOrderTax;
    public Currency RoundOrderCharge;
    public Currency RoundOrderAdd1;
    public Currency RoundOrderAdd2;
    public int BackOfficeInvoiceId;
    public String BackOfficeInvoiceNo;
    public Currency RoundInvoiceAmount;
    public Currency RoundInvoiceOtherDiscount;
    public Currency RoundInvoiceTax;
    public Currency RoundInvoiceCharge;
    public Currency RoundInvoiceDis1;
    public Currency RoundInvoiceDis2;
    public Currency RoundInvoiceDis3;
    public Currency RoundInvoiceAdd1;
    public Currency RoundInvoiceAdd2;
    public UUID InvoicePaymentTypeUniqueId;
    public int IsPromotion;
    public UUID PromotionUniqueId;
    public int SupervisorRefSDS;
    public String DcCodeSDS;
    public String SaleIdSDS;
    public int SaleNoSDS;
    public int DealerRefSDS;
    public String DealerCodeSDS;
    public String OrderNoSDS;
    public int AccYearSDS;
    public int SaleOfficeRefSDS;
    public int DcRefSDS;

    public Currency TotalOrderDis1;
    public Currency TotalOrderDis2;
    public Currency TotalOrderDis3;
    public Currency TotalOrderAdd1;
    public Currency TotalOrderAdd2;

    public Currency TotalInvoiceDis1;
    public Currency TotalInvoiceDis2;
    public Currency TotalInvoiceDis3;
    public Currency TotalInvoiceDisOther;
    public Currency TotalInvoiceAdd1;
    public Currency TotalInvoiceAdd2;
    public Currency TotalInvoiceAddOther;
    public Currency TotalInvoiceTax;
    public Currency TotalInvoiceCharge;

    public Currency TotalInvoiceDiscount;
    public Currency TotalInvoiceAdd;
    public Currency TotalPrice;
    public Currency TotalPriceWithPromo;
    public OrderTypeEnum DocumentType;
    public Currency TotalAmountNutCash;
    public Currency TotalAmountNutCheque;
    public Currency TotalCashDiscount;

    public String PaymentUsanceRef;
    public int UsanceDay;
    public UUID paymentUsanceId;
    public String paymentUsanceName;
    public String evcId;
    public double CashDuration;
    public double CheckDuration;
    public ArrayList<CustomerCallOrderLineGoodPackage> goodPackages;
    public ArrayList<CustomerCallOrderLinePromotion> Lines;
    public ArrayList<DiscountEvcItemStatuteData> discountEvcItemStatuteData;
    public ArrayList<CustomerCallOrderLinePromotion> LinesWithPromo;
    public ArrayList<DiscountEvcPrizeData> discountEvcPrize;

    /*TODO Asal remove
        public String SupervisorCodeSDS;
    public String StockDCCodeSDS;

     */

    public DiscountCallOrderData toDiscount(Context context){
        DiscountCallOrderData dis = new DiscountCallOrderData();
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        dis.saleDate= DateHelper.toString(this.SaleDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(context));

        dis.customerId = this.CustomerRef;

        dis.callUniqueId = this.UniqueId.toString();
        dis.orderUniqueId = this.UniqueId.toString();
        dis.totalOrderDis1 = (this.TotalOrderDis1 == null ? BigDecimal.ZERO : this.TotalOrderDis1.bigDecimalValue());
        dis.totalOrderDis2 = (this.TotalOrderDis2 == null ? BigDecimal.ZERO : this.TotalOrderDis2.bigDecimalValue());
        dis.totalOrderDis3 = (this.TotalOrderDis3 == null ? BigDecimal.ZERO : this.TotalOrderDis3.bigDecimalValue());
        dis.totalOrderAdd1 = (this.TotalOrderAdd1 == null ? BigDecimal.ZERO : this.TotalOrderAdd1.bigDecimalValue());
        dis.totalOrderAdd2 = (this.TotalOrderAdd2 == null ? BigDecimal.ZERO : this.TotalOrderAdd2.bigDecimalValue());

        dis.totalInvoiceDis1 = (this.TotalInvoiceDis1 == null ? BigDecimal.ZERO : this.TotalInvoiceDis1.bigDecimalValue());
        dis.totalInvoiceDis2 = (this.TotalInvoiceDis2 == null ? BigDecimal.ZERO : this.TotalInvoiceDis2.bigDecimalValue());
        dis.totalInvoiceDis3 = (this.TotalInvoiceDis3 == null ? BigDecimal.ZERO : this.TotalInvoiceDis3.bigDecimalValue());
        dis.totalInvoiceDisOther = (this.TotalInvoiceDisOther == null ? BigDecimal.ZERO : this.TotalInvoiceDisOther.bigDecimalValue());
        dis.totalInvoiceAdd1 = (this.TotalInvoiceAdd1 == null ? BigDecimal.ZERO : this.TotalInvoiceAdd1.bigDecimalValue());
        dis.totalInvoiceAdd2 = (this.TotalInvoiceAdd2 == null ? BigDecimal.ZERO : this.TotalInvoiceAdd2.bigDecimalValue());
        dis.totalInvoiceAddOther = (this.TotalInvoiceAddOther == null ? BigDecimal.ZERO : this.TotalInvoiceAddOther.bigDecimalValue());

        if (this.OrderTypeRef == 0)
            dis.OrderType = 2;
        else
            dis.OrderType = this.OrderTypeRef;
        dis.orderType = this.DocumentType;
        dis.invoicePaymentTypeId = this.OrderPaymentTypeRef;
        dis.orderPaymentTypeId = this.OrderPaymentTypeRef;
        dis.orderPaymentTypeUniqueId = this.OrderPaymentTypeId;
        dis.orderPaymentTypeName = this.OrderPaymentTypeName == null ? "" : this.OrderPaymentTypeName;
        dis.disTypeId = this.DisType;
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            dis.orderId = this.BackOfficeInvoiceId;
        else
            dis.orderId = this.BackOfficeOrderId;
        PaymentTypeOrderModel paymentOrder = new PaymentOrderTypeManager(context).getPaymentType(this.OrderPaymentTypeId);
        if (paymentOrder != null)
            dis.BuyTypeRef = paymentOrder.GroupBackOfficeId;
        try{
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.Varanegar){
                dis.disTypeId = 2;
        }
        } catch (Exception e){
            dis.disTypeId = 0;
            Timber.i("Load BackOfficeType :" + e.getMessage());
        }


        dis.dcCode= this.DcCodeSDS; //AppVariables.getInstance().getDCCode();
        dis.accYear = this.AccYearSDS;

        if (GlobalVariables.getApplicationType() == ApplicationType.DISTRIBUTION) {
            dis.DCRef = this.DcRefSDS;
            dis.dealerRef = this.DealerRefSDS;
            dis.dealerCode = this.DealerCodeSDS;
        } else {
            dis.DCRef = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.DcRef, SysConfigManager.cloud), -1);
            dis.dealerRef = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.DealerRef, SysConfigManager.cloud), -1);
            dis.dealerCode = SysConfigManager.getValue(sysConfigManager.read(ConfigKey.DealerCode, SysConfigManager.cloud));
        }

        dis.SaleOfficeRef = this.SaleOfficeRefSDS;//SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.SaleOfficeRef, SysConfigManager.cloud),-1);

        dis.stockDCRef = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.StockDCRef, SysConfigManager.cloud),-1);
        dis.stockDCCode= SysConfigManager.getValue(sysConfigManager.read(ConfigKey.StockDCCode, SysConfigManager.cloud));

        dis.supervisorRef = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.SupervisorRef, SysConfigManager.cloud),-1);
        dis.supervisorCode = SysConfigManager.getValue(sysConfigManager.read(ConfigKey.SupervisorCode, SysConfigManager.cloud));
        ArrayList<DiscountCallOrderLineData> callLineItemData = new ArrayList<DiscountCallOrderLineData>();
        Currency totalOrderNetAmount = new Currency(BigDecimal.ZERO);
        for (CustomerCallOrderLinePromotion  lineItem: this.Lines) {
            if(lineItem.TotalRequestQty != null && BigDecimal.ZERO.compareTo(lineItem.TotalRequestQty) < 0) {
                DiscountCallOrderLineData disline = lineItem.toDiscount(context);
                disline.customerId = dis.customerId;
                callLineItemData.add(disline);
              //  totalOrderNetAmount = totalOrderNetAmount.add(lineItem.InvoiceNetAmount);
            }
        }
        dis.totalOrderNetAmount = BigDecimal.ZERO;//totalOrderNetAmount.bigDecimalValue();
        dis.callOrderLineItemData = callLineItemData;
        if (this.BackOfficeOrderNo != null)
            dis.orderNo = Integer.parseInt(this.BackOfficeOrderNo);
        return dis;
    }

    /**
     * canver data online for
     * پیش نمایش
     * ثبت سفارش مشتری
     *WithPromo برای تخفیف و جوایز که شامل لاین های اصلی سفارش و جوایز
     * @param context
     * @param dis
     */
    public void setFromDiscount(Context context, DiscountCallOrderData dis){

        //this.distId = dis.distId;
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        BackOfficeType  backOfficeType = BackOfficeType.Varanegar;
        try{
            backOfficeType = sysConfigManager.getBackOfficeType();
        }catch (Exception e){
            Timber.i("Load BackOfficeType :" + e.getMessage());
        }
        this.OrderPaymentTypeId = dis.orderPaymentTypeUniqueId;
        this.OrderPaymentTypeName = dis.orderPaymentTypeName;
        this.OrderPaymentTypeRef = dis.orderPaymentTypeId;
        this.LinesWithPromo = new ArrayList<>();
        this.TotalInvoiceDis1 = Currency.ZERO;
        this.TotalInvoiceDis2 = Currency.ZERO;
        this.TotalInvoiceDis3 = Currency.ZERO;
        this.TotalInvoiceDisOther = Currency.ZERO;
        this.TotalInvoiceAdd1 = Currency.ZERO;
        this.TotalInvoiceAdd2 = Currency.ZERO;
        this.TotalInvoiceAddOther = Currency.ZERO;
        this.TotalInvoiceTax = Currency.ZERO;
        this.TotalInvoiceCharge = Currency.ZERO;
        this.TotalPriceWithPromo = Currency.ZERO;
        this.TotalAmountNutCash = Currency.ZERO;
        this.TotalAmountNutCheque = Currency.ZERO;
        this.TotalCashDiscount =Currency.ZERO;

        if (dis.callLineItemDataWithPromo == null)
            dis.callLineItemDataWithPromo = new ArrayList<>();
        for (DiscountCallOrderLineData item :dis.callLineItemDataWithPromo) {
            CustomerCallOrderLinePromotion calitem = new CustomerCallOrderLinePromotion(context,item, this.Lines);
            this.LinesWithPromo.add(calitem);

            if (backOfficeType == BackOfficeType.Varanegar) {
                this.TotalInvoiceDis1 = this.TotalInvoiceDis1.add(calitem.InvoiceDis1);
                this.TotalInvoiceDis2 = this.TotalInvoiceDis2.add(calitem.InvoiceDis2);
                this.TotalInvoiceDis3 = this.TotalInvoiceDis3.add(calitem.InvoiceDis3);
                this.TotalInvoiceDisOther = this.TotalInvoiceDisOther.add(calitem.InvoiceDisOther);
            }
            else{
                this.TotalInvoiceDis1 = this.TotalInvoiceDis1.add(calitem.InvoiceDis1);
                this.TotalInvoiceTax = this.TotalInvoiceTax.add(calitem.InvoiceTax);
                this.TotalInvoiceCharge = this.TotalInvoiceCharge.add(calitem.InvoiceCharge);
            }
            if (GlobalVariables.getIsThirdParty() && VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                this.TotalInvoiceAdd1 = this.TotalInvoiceAdd1.add((calitem.InvoiceAdd1).multiply(calitem.TotalRequestQty));
            } else {
                this.TotalInvoiceAdd1 = this.TotalInvoiceAdd1.add(calitem.InvoiceAdd1);
                this.TotalAmountNutCash = this.TotalAmountNutCash.add(calitem.AmountNutCash);
                this.TotalAmountNutCheque = this.TotalAmountNutCheque.add(calitem.AmountNutCheque);
                this.TotalCashDiscount = this.TotalCashDiscount.add(calitem.CashDiscount);
            }
            this.TotalInvoiceAdd2 = this.TotalInvoiceAdd2.add(calitem.InvoiceAdd2);
            this.TotalInvoiceAddOther = this.TotalInvoiceAddOther.add(calitem.InvoiceAddOther);
            this.TotalPriceWithPromo = this.TotalPriceWithPromo.add(calitem.InvoiceAmount);
        }

        this.TotalInvoiceAdd = this.TotalInvoiceAdd1.add(this.TotalInvoiceAdd2)
                .add(this.TotalInvoiceTax).add(this.TotalInvoiceCharge).add(TotalInvoiceAddOther);
        this.TotalInvoiceDiscount = this.TotalInvoiceDis1.add(this.TotalInvoiceDis2)
                .add(this.TotalInvoiceDis3).add(this.TotalInvoiceDisOther);

        this.discountEvcItemStatuteData = dis.callOrderEvcItemStatutes;
        this.CustomerName = "";
        this.CustomerCode = "";
        this.discountEvcPrize = dis.discountEvcPrize;
        this.UsanceDay = dis.UsanceDay;
        this.PaymentUsanceRef = dis.PaymentUsanceRef;
        this.CashDuration = dis.CashDuration;
        this.CheckDuration = dis.CheckDuration;
    }


}
