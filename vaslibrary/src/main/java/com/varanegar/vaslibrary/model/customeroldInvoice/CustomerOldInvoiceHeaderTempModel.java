package com.varanegar.vaslibrary.model.customeroldInvoice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/15/2018.
 */
@Table
public class CustomerOldInvoiceHeaderTempModel extends BaseModel {
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
}
