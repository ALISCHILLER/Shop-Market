package com.varanegar.vaslibrary.model.customeroldInvoice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.math.BigDecimal;
import java.security.cert.CRLReason;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CustomerOldInvoiceDetailModel extends BaseModel {
    @Column
    public String SaleId;
    @Column
    @NotNull
    public UUID ProductId;
    @Column
    public int UnitCapasity;
    @Column
    public int UnitRef;
    @Column
    public BigDecimal UnitQty;
    @Column
    public BigDecimal TotalQty;
    @Column
    public String UnitName;
    @Column
    public Currency UnitPrice;
    @Column
    public String  PriceId;
    @Column
    public int CPriceRef;
    @Column
    public Currency Amount;
    @Column
    public Currency AmountNut;
    @Column
    public Currency Discount;
    @Column
    public int PrizeType;
    @Column
    public Currency SupAmount;
    @Column
    public Currency AddAmount;
    @Column
    public Currency CustPrice;
    @Column
    public Currency UserPrice;
    @Column
    public Currency Charge;
    @Column
    public Currency Tax;
    @Column
    public int RowOrder;
    @Column
    public int ProductCtgrId;
    @Column
    public int FreeReasonId;
    @Column
    public boolean IsDeleted;
    @Column
    public Currency Dis1;
    @Column
    public Currency Dis2;
    @Column
    public Currency Dis3;
    @Column
    public Currency OtherDiscount;
    @Column
    public Currency Add1;
    @Column
    public Currency Add2;
    @Column
    public Currency OtherAddition;
    @Column
    public Currency PreviousRetQty;
    @Column
    public String Item;


    public CustomerOldInvoiceDetailTempModel convert() {
        CustomerOldInvoiceDetailTempModel customerOldInvoiceDetailTempModel = new CustomerOldInvoiceDetailTempModel();
        customerOldInvoiceDetailTempModel.UniqueId = UniqueId;
        customerOldInvoiceDetailTempModel.SaleId = SaleId;
        customerOldInvoiceDetailTempModel.ProductId = ProductId;
        customerOldInvoiceDetailTempModel.UnitCapasity = UnitCapasity;
        customerOldInvoiceDetailTempModel.UnitRef = UnitRef;
        customerOldInvoiceDetailTempModel.UnitQty = UnitQty;
        customerOldInvoiceDetailTempModel.TotalQty = TotalQty;
        customerOldInvoiceDetailTempModel.UnitName = UnitName;
        customerOldInvoiceDetailTempModel.UnitPrice = UnitPrice;
        customerOldInvoiceDetailTempModel.PriceId = PriceId;
        customerOldInvoiceDetailTempModel.CPriceRef = CPriceRef;
        customerOldInvoiceDetailTempModel.Amount = Amount;
        customerOldInvoiceDetailTempModel.AmountNut = AmountNut;
        customerOldInvoiceDetailTempModel.Discount = Discount;
        customerOldInvoiceDetailTempModel.PrizeType = PrizeType;
        customerOldInvoiceDetailTempModel.SupAmount = SupAmount;
        customerOldInvoiceDetailTempModel.AddAmount = AddAmount;
        customerOldInvoiceDetailTempModel.CustPrice = CustPrice;
        customerOldInvoiceDetailTempModel.UserPrice = UserPrice;
        customerOldInvoiceDetailTempModel.Charge = Charge;
        customerOldInvoiceDetailTempModel.Tax = Tax;
        customerOldInvoiceDetailTempModel.RowOrder = RowOrder;
        customerOldInvoiceDetailTempModel.ProductCtgrId = ProductCtgrId;
        customerOldInvoiceDetailTempModel.FreeReasonId = FreeReasonId;
        customerOldInvoiceDetailTempModel.IsDeleted = IsDeleted;

        return customerOldInvoiceDetailTempModel;
    }

}
