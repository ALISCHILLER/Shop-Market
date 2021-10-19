package varanegar.com.vdmclient.call;

import com.varanegar.java.util.Currency;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/19/2018.
 */

public class CustomerCallOrderLinePromotion {
    public UUID OrderId;
    public UUID ProductId;
    public String ProductCode;
    public String ProductName;
    public int ProductRef;

    public String UnitName;
    public String QtyCaption;
    public int ConvertFactory;

    public Currency UnitPrice;
    public UUID PriceId;
    public int SortId;
    public boolean IsRequestFreeItem;
    public BigDecimal RequestBulkQty;
    public UUID RequesBulkQtyUnitUniqueId;
    public UUID UnitUniqieId;

    public Currency RequestAmount;
    public Currency RequestNetAmount;
    public Currency RequestDis1;
    public Currency RequestDis2;
    public Currency RequestDis3;
    public Currency RequestAdd1;
    public Currency RequestAdd2;
    public Currency RequestTax;
    public Currency RequestCharge;
    public Currency RequestOtherDiscount;
    public BigDecimal TotalRequestQty;

    public Currency InvoiceAmount;
    public Currency InvoiceNetAmount;
    public Currency InvoiceDis1;
    public Currency InvoiceDis2;
    public Currency InvoiceDis3;
    public Currency InvoiceAdd1;
    public Currency InvoiceAdd2;
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
    public UUID UniqueId;
}
