package com.varanegar.vaslibrary.promotion;

import android.content.Context;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.manager.FreeReasonManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.UnitManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountSDSManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountVnLiteManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.unit.UnitModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;

/**
 * Created by Asal on 11/07/2017.
 */

public class CustomerCallOrderLinePromotion extends BaseModel {
    public String OrderId;
    public UUID ProductId;
    public String ProductCode;
    public String ProductName;
    public int ProductRef;

    public String UnitName;
    public String QtyCaption;
    public int ConvertFactory;
    public Currency amount;
    public Currency UnitPrice;
    public String PriceId;
    public int SortId;
    public boolean IsRequestFreeItem;
    public BigDecimal RequestBulkQty;
    public UUID RequesBulkQtyUnitUniqueId;
    public UUID UnitUniqieId;
    public Currency AdjustmentPrice;

    public Currency RequestAmount;
    public Currency RequestNetAmount;
    public Currency RequestDis1;
    public Currency RequestDis2;
    public Currency RequestDis3;
    public Currency RequestDisOther;
    public Currency RequestAdd1;
    public Currency RequestAdd2;
    public Currency RequestAddOther;
    public Currency RequestTax;
    public Currency RequestCharge;
    public Currency RequestOtherDiscount;
    public BigDecimal TotalRequestQty;

    public Currency InvoiceAmount;
    public Currency InvoiceNetAmount;
    public Currency InvoiceDis1;
    public Currency custPrice;
    public Currency InvoiceDis2;
    public Currency InvoiceDis3;
    public Currency InvoiceDisOther;
    public Currency InvoiceAdd1;
    public Currency TakhfifatKol;
    public Currency Fee;
    public Currency FeeKol;
    public String zterm;
    public Currency InvoiceAdd2;
    public Currency InvoiceAddOther;
    public Currency InvoiceTax;
    public Currency InvoiceCharge;
    public Currency InvoiceOtherDiscount;
    public Currency TotalInvoiceDis;
    public Currency TotalInvoiceAdd;
    public BigDecimal TotalInvoiceQty;

    public String Comment;
    public UUID EVCId;
    public UUID FreeReasonId;
    public String FreeReasonName;
    public BigDecimal InvoiceBulkQty;
    public UUID InvoiceBulkQtyUnitUniqueId;

    public boolean IsRequestPrizeItem;
    public int DiscountRef;
    public UUID DiscountId;
    public int PayDuration;
    public int RuleNo;
    public String SaleNo;
    public String ReferenceNo;
    public UUID ReturnReasonId;
    public Date OrderDate;

    public Currency AmountCash;
    public Currency AmountCheque;
    public Currency AmountNutCash;
    public Currency AmountNutCheque;
    public Currency AmountImmediate;
    public Currency AmountNutImmediate;


    public Currency CashDiscount;
    public Currency ChequeDiscount;
    public CustomerCallOrderLinePromotion() {

    }

    public CustomerCallOrderLinePromotion(Context conext, DiscountCallOrderLineData lineData,
                                          ArrayList<CustomerCallOrderLinePromotion> baseLine) {
        SysConfigManager sysConfigManager = new SysConfigManager(conext);
        BackOfficeType backOfficeType = BackOfficeType.Varanegar;
        try {
            backOfficeType = sysConfigManager.getBackOfficeType();
        } catch (Exception e) {
            Timber.i("Load BackOfficeType :" + e.getMessage());
        }

        this.UniqueId = (lineData.orderLineId == null ? UUID.randomUUID() : lineData.orderLineId);
        this.EVCId = UUID.fromString(lineData.evcId);
        this.ProductCode = lineData.productCode;
        this.ProductName = lineData.productName;
        this.PayDuration = lineData.PayDuration;
        this.RuleNo = lineData.RuleNo;
        this.TakhfifatKol= new Currency(lineData.TakhfifatKol == null ? BigDecimal.ZERO : lineData.TakhfifatKol);
        this.custPrice= new Currency(lineData.custPrice == null ? BigDecimal.ZERO : lineData.custPrice);
        this.Fee= new Currency(lineData.Fee == null ? BigDecimal.ZERO : lineData.Fee);
        this.FeeKol= new Currency(lineData.FeeKol == null ? BigDecimal.ZERO : lineData.FeeKol);
        this.zterm= lineData.zterm;
        this.InvoiceAmount = lineData.totalInvoiceAmount != null ? new Currency(lineData.totalInvoiceAmount) : new Currency(0);
        this.InvoiceNetAmount = new Currency(lineData.totalInvoiceNetAmount);
        this.InvoiceAdd1 = new Currency(
                lineData.totalInvoiceAdd1Amount == null ? BigDecimal.ZERO : lineData.totalInvoiceAdd1Amount);
        this.InvoiceAdd2 = new Currency(
                lineData.totalInvoiceAdd2Amount == null ? BigDecimal.ZERO : lineData.totalInvoiceAdd2Amount);
        this.InvoiceAddOther = new Currency(
                lineData.totalInvoiceAddOther == null ? BigDecimal.ZERO : lineData.totalInvoiceAddOther);
        this.AdjustmentPrice = lineData.adjustmentPrice == null ? null : new Currency(lineData.adjustmentPrice);

        this.AmountCash = new Currency(lineData.amountCash == null ? BigDecimal.ZERO : lineData.amountCash);
        this.AmountCheque = new Currency(lineData.amountCheque == null ? BigDecimal.ZERO : lineData.amountCheque);
        this.AmountNutCash = new Currency(lineData.amountNutCash == null ? BigDecimal.ZERO : lineData.amountNutCash);
        this.AmountNutCheque = new Currency(lineData.amountNutCheque == null ? BigDecimal.ZERO : lineData.amountNutCheque);
        this.AmountImmediate = new Currency(lineData.amountImmediate==null?BigDecimal.ZERO:lineData.amountImmediate );
        this.AmountNutImmediate = new Currency(lineData.amountNutImmediate==null?BigDecimal.ZERO:lineData.amountNutImmediate );

        this.ChequeDiscount =new Currency(lineData.chequeDiscount==null?BigDecimal.ZERO:lineData.chequeDiscount);
        this.CashDiscount =new Currency(lineData.cashDiscount==null?BigDecimal.ZERO:lineData.cashDiscount);

        try {
            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Varanegar)) {
                this.InvoiceCharge = Currency.ZERO;
                this.InvoiceTax = Currency.ZERO;
                this.InvoiceDisOther = new Currency(
                        lineData.invoiceDisOther == null ? BigDecimal.ZERO : lineData.invoiceDisOther);
                this.InvoiceDis1 = new Currency(lineData.invoiceDis1 == null ? BigDecimal.ZERO : lineData.invoiceDis1);
                this.InvoiceDis2 = new Currency(lineData.invoiceDis2 == null ? BigDecimal.ZERO : lineData.invoiceDis2);
                this.InvoiceDis3 = new Currency(lineData.invoiceDis3 == null ? BigDecimal.ZERO : lineData.invoiceDis3);
            } else {
                this.TotalInvoiceDis = Currency.ZERO;
                this.InvoiceDis1 = new Currency(
                        lineData.totalInvoiceDiscount == null ? BigDecimal.ZERO : lineData.totalInvoiceDiscount);
                this.InvoiceCharge = new Currency(
                        lineData.totalInvoiceCharge == null ? BigDecimal.ZERO : lineData.totalInvoiceCharge);
                this.InvoiceTax = new Currency(
                        lineData.totalInvoiceTax == null ? BigDecimal.ZERO : lineData.totalInvoiceTax);
            }

        } catch (Exception e) {
            Timber.i("DiscountCallOrderLineData toDiscount :" + e.getMessage());
        }
        // this.QtyCaption = lineData.invoiceTotalQty.toString();
        if (lineData.isFreeItem == 1)
            this.IsRequestFreeItem = true;
        else
            this.IsRequestFreeItem = false;
        this.UnitPrice = new Currency(lineData.unitPrice);
        this.amount = new Currency(lineData.amount);
        UUID productId = UUID.fromString(new ProductManager(conext).getIdByBackofficeId(lineData.productId));
        this.ProductId = productId;
        this.ProductRef = lineData.productId;

        if (lineData.freeReasonId != 0) {
            FreeReasonModel freeReasonModel = new FreeReasonManager(conext).getByBackofficeId(lineData.freeReasonId);
            if (freeReasonModel == null)
                Timber.d("freeReasonModel is null for backOfficeId = " + lineData.freeReasonId);
            this.FreeReasonId = freeReasonModel.UniqueId;
            this.FreeReasonName = freeReasonModel.FreeReasonName;
        }

        this.DiscountRef = lineData.disRef;

        if (lineData.disRef != 0) {

            if (backOfficeType.equals(BackOfficeType.Rastak)) {
                String disId = new DiscountVnLiteManager(conext).getIdByBackofficeId(lineData.disRef);
                if (disId == null || disId.equals(""))
                    Timber.d("disId is empty or null for backOfficeId = " + lineData.disRef);
                if (disId != null)
                    this.DiscountId = UUID.fromString(disId);
                this.TotalRequestQty = lineData.invoiceTotalQty;

                this.QtyCaption = lineData.invoiceBigQty.toString();
                if (lineData.invoiceSmallQty != null)
                    this.QtyCaption += ":" + lineData.invoiceSmallQty.toString();

                UnitModel unitModel = new UnitManager(conext).getByBackofficeId((int) lineData.invoiceBigQtyId);
                if (unitModel != null)
                    this.UnitName = unitModel.UnitName;
                else
                    Timber.d("unitModel is null for backOfficeId = " + ((int) lineData.invoiceBigQtyId));
                if (lineData.invoiceSmallQtyId != 0) {
                    unitModel = new UnitManager(conext).getByBackofficeId((int) lineData.invoiceSmallQtyId);
                    if (unitModel != null)
                        this.UnitName += ":" + unitModel.UnitName;
                    else
                        Timber.d("unitModel is null for backOfficeId = " + ((int) lineData.invoiceSmallQtyId));
                }
            } else {
                String disId = new DiscountSDSManager(conext).getIdByBackofficeId(lineData.disRef);
                if (disId == null || disId.equals(""))
                    Timber.d("disId is empty or null for backOfficeId = " + lineData.disRef);
                if (disId != null)
                    this.DiscountId = UUID.fromString(disId);

                this.TotalRequestQty = lineData.invoiceTotalQty;
                this.QtyCaption = lineData.invoiceBigQty.toString();
                this.ConvertFactory = lineData.unitCapacity;
                /*
                 * this.TotalRequestQty = lineData.invoiceTotalQty; this.QtyCaption =
                 * lineData.invoiceTotalQty.toString();
                 */
                UnitModel unitModel = new UnitManager(conext).getByBackofficeId((int) lineData.invoiceBigQtyId);
                if (unitModel != null) {
                    this.UnitName = unitModel.UnitName;
                    this.UnitUniqieId = unitModel.UniqueId;
                } else
                    Timber.d("unitModel is null for backOfficeId = " + ((int) lineData.invoiceBigQtyId));
            }

        } else {

            for (CustomerCallOrderLinePromotion baseLineData : baseLine) {
                if (baseLineData.ReturnReasonId != null) {
                    if (baseLineData.TotalRequestQty.equals(lineData.invoiceTotalQty) &&
                            baseLineData.ProductId.equals(productId) &&
                            baseLineData.IsRequestPrizeItem == lineData.isRequestPrizeItem &&
                            ((baseLineData.FreeReasonId != null ? baseLineData.FreeReasonId : "0").equals(this.FreeReasonId != null ? this.FreeReasonId : "0"))) {
                        this.QtyCaption = baseLineData.QtyCaption;
                        this.UnitName = baseLineData.UnitName;
                        this.TotalRequestQty = baseLineData.TotalRequestQty;

                        break;
                    }
                } else {
                    if (baseLineData.ProductId.equals(productId)
                            && baseLineData.IsRequestPrizeItem == lineData.isRequestPrizeItem && ((baseLineData.FreeReasonId != null ? baseLineData.FreeReasonId : "0").equals(this.FreeReasonId != null ? this.FreeReasonId : "0"))) {
                        this.QtyCaption = baseLineData.QtyCaption;
                        this.UnitName = baseLineData.UnitName;
                        this.TotalRequestQty = baseLineData.TotalRequestQty;

                        break;
                    }
                }
            }

        }
    }

    public DiscountCallOrderLineData toDiscount(Context context) {
        DiscountCallOrderLineData dis = new DiscountCallOrderLineData();
        dis.saleNo = SaleNo;
        if (OrderId != null)
            dis.orderUniqueId = this.OrderId;
        dis.referenceNo = ReferenceNo;
        dis.orderDate = DateHelper.toString(this.OrderDate, DateFormat.Date, Locale.ENGLISH);
        dis.returnReasonId = ReturnReasonId;
        dis.orderLineId = this.UniqueId;
        dis.sortId = this.SortId;
        dis.productId = new ProductManager(context).getBackOfficeId(this.ProductId);
        dis.PayDuration = this.PayDuration;
        dis.RuleNo = this.RuleNo;
        dis.invoiceTotalQty = this.TotalRequestQty == null ? BigDecimal.ZERO : this.TotalRequestQty;
        dis.totalInvoiceCharge = BigDecimal.ZERO;
        dis.totalInvoiceTax = BigDecimal.ZERO;
        dis.totalInvoiceAdd1Amount = BigDecimal.ZERO;
        dis.totalInvoiceAdd2Amount = BigDecimal.ZERO;

        if (this.IsRequestFreeItem)
            dis.isFreeItem = 1;
        else
            dis.isFreeItem = 0;

        dis.freeReasonId = 0;
        if (this.FreeReasonId != null)
            dis.freeReasonId = new FreeReasonManager(context).getBackOfficeId(this.FreeReasonId);
        dis.weight = new BigDecimal(0);// TODO ASal;
        dis.volume = 0;// TODO ASal
        dis.totalRequestCharge = new BigDecimal(0);
        dis.totalRequestTax = new BigDecimal(0);
        dis.unitPrice = this.UnitPrice == null ? BigDecimal.ZERO : this.UnitPrice.bigDecimalValue();
        dis.userprice = new BigDecimal(0);
        dis.isRequestPrizeItem = this.IsRequestPrizeItem;
        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(context);
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        try {
            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Varanegar) || sysConfigManager.getBackOfficeType().equals(BackOfficeType.ThirdParty)) {
                ProductUnitViewModel smallunit = productUnitViewManager.getSmallUnit(this.ProductId);
                dis.invoiceBigQty = this.TotalRequestQty;
                if (smallunit != null) {
                    dis.invoiceBigQtyId = smallunit.UnitRef;
                }
            } else {
                ArrayList<String> lsAmount = productUnitViewManager.getAmountLargeSmall(this.ProductId,
                        this.TotalRequestQty);
                dis.invoiceSmallQty = BigDecimal.ZERO;
                dis.invoiceBigQty = BigDecimal.ZERO;
                if (lsAmount.size() > 0) {
                    // Set Qty
                    String[] strqty = lsAmount.get(0).split(":");
                    if (strqty.length == 1) {
                        dis.invoiceSmallQty = new BigDecimal(strqty[0]);
                    }
                    if (strqty.length == 2) {
                        dis.invoiceBigQty = new BigDecimal(strqty[0]);
                        dis.invoiceSmallQty = new BigDecimal(strqty[1]);
                    }

                    // Set UnitName
                    strqty = lsAmount.get(1).split(":");
                    if (strqty.length == 1) {
                        dis.invoiceSmallQtyName = strqty[0];
                    }
                    if (strqty.length == 2) {
                        dis.invoiceBigQtyName = strqty[0];
                        dis.invoiceSmallQtyName = strqty[1];
                    }

                    // Set UnitRef
                    strqty = lsAmount.get(2).split(":");
                    if (strqty.length == 1) {
                        dis.invoiceSmallQtyId = Long.parseLong(strqty[0]);
                    }
                    if (strqty.length == 2) {
                        dis.invoiceBigQtyId = Long.parseLong(strqty[0]);
                        dis.invoiceSmallQtyId = Long.parseLong(strqty[1]);
                    }
                }
            }
        } catch (Exception e) {
            Timber.i("DiscountCallOrderLineData toDiscount :" + e.getMessage());
        }
        return dis;
    }

}
