package com.varanegar.vaslibrary.model.customeroldInvoice;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CustomerOldInvoiceHeaderModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public String PersonnelId;
    @Column
    public int StockBackOfficeId;
    @Column
    public UUID StockId;
    @Column
    public Currency Amount;
    @Column
    public Currency Dis1Amount;
    @Column
    public Currency Dis2Amount;
    @Column
    public Currency Dis3Amount;
    @Column
    public Currency Add1Amount;
    @Column
    public Currency Add2Amount;
    @Column
    public Currency ChargeAmount;
    @Column
    public Currency TaxAmount;
    @Column
    public int OrderBackOfficeTypeId;
    @Column
    public UUID PaymentTypeOrderUniqueId;
    @Column
    public int PaymentUsanceRef;
    @Column
    public int  OrderBackOfficeId;
    @Column
    public Currency PayAmount;
    @Column
    public Currency TotalAmount;
    @Column
    public String SaleNo;
    @Column
    public Date SaleDate;
    @Column
    public String SalePDate;
    @Column
    public String SaleRef;
    @Column
    public String SaleVocherNo;
    @Column
    public String GoodsGroupTreeXML;
    @Column
    public String GoodsDetailXML;
    @Column
    public String GoodsMainSubTypeDetailXML;
    @Column
    public int CustCtgrRef;
    @Column
    public int CustActRef;
    @Column
    public int CustLevelRef;
    @Column
    public int SaleOfficeRef;
    @Column
    public String OrderType;
    @Column
    public String BuyTypeId;
    @Column
    @SerializedName("dcRef")
    public int DCRef;
    @Column
    public String DisType;
    @Column
    public String AccYear;
    @Column
    public int DCSaleOfficeRef;
    @Column
    public String StockDCCode;
    @Column
    public String DealerCode;
    @Column
    public String SupervisorCode;
    @Column
    public String OrderId;
    @Column
    public String OrderNo;
    @Column
    public int DealerRef;
    @Column
    public int CustRef;
    @Column
    public UUID DealerId;
    @Column
    public Currency CashAmount;
    @Column
    public Currency ChequeAmount;
    @Column
    public int BuyTypeRef;
    @Column
    public int OrderRef;
    @Column
    public String DealerName;

    public CustomerOldInvoiceHeaderTempModel convert() {
        CustomerOldInvoiceHeaderTempModel customerOldInvoiceHeaderTempModel = new CustomerOldInvoiceHeaderTempModel();
        customerOldInvoiceHeaderTempModel.UniqueId = UniqueId;
        customerOldInvoiceHeaderTempModel.CustomerId = CustomerId;
        customerOldInvoiceHeaderTempModel.PersonnelId = PersonnelId;
        customerOldInvoiceHeaderTempModel.StockBackOfficeId = StockBackOfficeId;
        customerOldInvoiceHeaderTempModel.StockId = StockId;
        customerOldInvoiceHeaderTempModel.Amount = Amount;
        customerOldInvoiceHeaderTempModel.Dis1Amount = Dis1Amount;
        customerOldInvoiceHeaderTempModel.Dis2Amount = Dis2Amount;
        customerOldInvoiceHeaderTempModel.Dis3Amount = Dis3Amount;
        customerOldInvoiceHeaderTempModel.Add1Amount = Add1Amount;
        customerOldInvoiceHeaderTempModel.Add2Amount = Add2Amount;
        customerOldInvoiceHeaderTempModel.ChargeAmount = ChargeAmount;
        customerOldInvoiceHeaderTempModel.TaxAmount = TaxAmount;
        customerOldInvoiceHeaderTempModel.OrderBackOfficeTypeId = OrderBackOfficeTypeId;
        customerOldInvoiceHeaderTempModel.PaymentTypeOrderUniqueId = PaymentTypeOrderUniqueId;
        customerOldInvoiceHeaderTempModel.PaymentUsanceRef = PaymentUsanceRef;
        customerOldInvoiceHeaderTempModel.OrderBackOfficeId = OrderBackOfficeId;
        customerOldInvoiceHeaderTempModel.PayAmount = PayAmount;
        customerOldInvoiceHeaderTempModel.TotalAmount = TotalAmount;
        customerOldInvoiceHeaderTempModel.SaleNo = SaleNo;
        customerOldInvoiceHeaderTempModel.SaleDate = SaleDate;
        customerOldInvoiceHeaderTempModel.SalePDate= SalePDate;
        customerOldInvoiceHeaderTempModel.SaleRef = SaleRef;
        customerOldInvoiceHeaderTempModel.SaleVocherNo = SaleVocherNo;
        customerOldInvoiceHeaderTempModel.GoodsGroupTreeXML = GoodsGroupTreeXML;
        customerOldInvoiceHeaderTempModel.GoodsDetailXML = GoodsDetailXML;
        customerOldInvoiceHeaderTempModel.GoodsMainSubTypeDetailXML = GoodsMainSubTypeDetailXML;
        customerOldInvoiceHeaderTempModel.CustCtgrRef = CustCtgrRef;
        customerOldInvoiceHeaderTempModel.CustActRef = CustActRef;
        customerOldInvoiceHeaderTempModel.CustLevelRef = CustLevelRef;
        customerOldInvoiceHeaderTempModel.SaleOfficeRef = SaleOfficeRef;
        customerOldInvoiceHeaderTempModel.OrderType = OrderType;
        customerOldInvoiceHeaderTempModel.BuyTypeId = BuyTypeId;
        customerOldInvoiceHeaderTempModel.DCRef = DCRef;
        customerOldInvoiceHeaderTempModel.DisType = DisType;
        customerOldInvoiceHeaderTempModel.AccYear = AccYear;
        customerOldInvoiceHeaderTempModel.DCSaleOfficeRef = DCSaleOfficeRef;
        customerOldInvoiceHeaderTempModel.StockDCCode = StockDCCode;
        customerOldInvoiceHeaderTempModel.DealerCode = DealerCode;
        customerOldInvoiceHeaderTempModel.SupervisorCode = SupervisorCode;
        customerOldInvoiceHeaderTempModel.OrderId = OrderId;
        customerOldInvoiceHeaderTempModel.OrderNo = OrderNo;
        customerOldInvoiceHeaderTempModel.DealerRef = DealerRef;
        customerOldInvoiceHeaderTempModel.CustRef = CustRef;
        customerOldInvoiceHeaderTempModel.DealerId = DealerId;
        customerOldInvoiceHeaderTempModel.CashAmount = CashAmount;
        customerOldInvoiceHeaderTempModel.ChequeAmount = ChequeAmount;
        customerOldInvoiceHeaderTempModel.BuyTypeRef = BuyTypeRef;
        customerOldInvoiceHeaderTempModel.OrderRef = OrderRef;
        customerOldInvoiceHeaderTempModel.DealerName = DealerName;
        return customerOldInvoiceHeaderTempModel;
    }

}
