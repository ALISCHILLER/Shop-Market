package com.varanegar.vaslibrary.model.call;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/1/2018.
 */

public class OrderBaseModel extends BaseModel {
    public UUID CustomerCallUniqueId;
    @Column
    public int RowNo;
    @NotNull
    @Column
    public UUID CustomerUniqueId;
    @Column
    public int DistBackOfficeId;
    @Column
    public int DisType;
    @Column
    public String Comment;
    @Column
    public String LocalPaperNo;
    @Column
    public String BackOfficeOrderNo;
    @Column
    public Date SaleDate;
    @Column
    public int BackOfficeOrderId;
    @Column
    public UUID BackOfficeOrderTypeId;
    @Column
    public UUID OrderPaymentTypeUniqueId;
    @Column
    public Currency RoundOrderOtherDiscount;
    @Column
    public Currency RoundOrderDis1;
    @Column
    public Currency RoundOrderDis2;
    @Column
    public Currency RoundOrderDis3;
    @Column
    public Currency RoundOrderTax;
    @Column
    public Currency RoundOrderCharge;
    @Column
    public Currency RoundOrderAdd1;
    @Column
    public Currency RoundOrderAdd2;
    @Column
    public int BackOfficeInvoiceId;
    @Column
    public int BackOfficeInvoiceNo;
//    @Column
//    public Currency RoundInvoiceAmount;
//    @Column
//    public Currency RoundInvoiceOtherDiscount;
//    @Column
//    public Currency RoundInvoiceTax;
//    @Column
//    public Currency RoundInvoiceCharge;
//    @Column
//    public Currency RoundInvoiceDis1;
//    @Column
//    public Currency RoundInvoiceDis2;
//    @Column
//    public Currency RoundInvoiceDis3;
//    @Column
//    public Currency RoundInvoiceAdd1;
//    @Column
//    public Currency RoundInvoiceAdd2;
//    @Column
    public UUID InvoicePaymentTypeUniqueId;
    @Column
    public int IsPromotion;
    @Column
    public UUID PromotionUniqueId;
    @Column
    public String StockDCCodeSDS;
    @Column
    public int SupervisorRefSDS;
    @Column
    public String SupervisorCodeSDS;
    @Column
    public String DcCodeSDS;
    @Column
    public String SaleIdSDS;
    @Column
    public int SaleNoSDS;
    @Column
    public int DealerRefSDS;
    @Column
    public String DealerCodeSDS;
    @Column
    public String OrderNoSDS;
    @Column
    public int AccYearSDS;
    @Column
    @SerializedName("dcRefSDS")
    public int DCRefSDS;
    @Column
    public int SaleOfficeRefSDS;
    @Column
    public int StockDCRefSDS;
    @Column
    public UUID CallActionStatusUniqueId;
    @Column
    public UUID SubSystemTypeUniqueId;
    @Column
    public UUID OrderTypeUniqueId;
    @Column
    public UUID PriceClassId;
    @Column
    public Date DeliveryDate;
    @Column
    public Date StartTime;
    @Column
    public Date EndTime;
    @Column
    public boolean IsInvoice;
    @Column
    public String DealerName;
    @Column
    @SerializedName("dealerMobail")
    public String DealerMobile;
    @Column
    public double CheckDuration;
    @Column
    public double CashDuration;
    @Column
    public String PinCode;
    @Column
    public String PinCode2;
}
