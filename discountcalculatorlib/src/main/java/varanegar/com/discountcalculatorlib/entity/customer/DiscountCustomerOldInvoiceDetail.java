package varanegar.com.discountcalculatorlib.entity.customer;

import java.math.BigDecimal;

public class DiscountCustomerOldInvoiceDetail {

    public int SaleId;
    public int ProductId;
    public int UnitCapasity;
    public int UnitRef;
    public String UnitName;
    public int UnitQty;
    public int TotalQty;
    public long priceId;
    public BigDecimal unitPrice;

    public int saleNo;


    public DiscountCustomerOldInvoiceDetail()
    {}

    public DiscountCustomerOldInvoiceDetail(int saleId, int productId, int unitCapasity, int unitRef, String unitName,
                                    int unitQty, int totalQty, long priceId, BigDecimal unitPrice, int saleNo)
    {
        this.SaleId = saleId;
        this.ProductId = productId;
        this.UnitCapasity = unitCapasity;
        this.UnitRef = unitRef;
        this.UnitName = unitName;
        this.UnitQty = unitQty;
        this.TotalQty = totalQty;
        this.priceId = priceId;
        this.unitPrice = unitPrice;
        this.saleNo = saleNo;
    }
}
