package varanegar.com.discountcalculatorlib.entity.customer;

public class DiscountCustomerOldInvoiceHeader {

    public int CustomerId;
    public int DealerId;
    public int StockId;
    public int SaleId;
    public int SaleNo;
    public String SaleDate;
    public int SaleVocherNo;
    public String DistributerName;
    public String SaleHeaderInfo;

    public DiscountCustomerOldInvoiceHeader()
    {}

    public DiscountCustomerOldInvoiceHeader(int customerId, int dealerId, int stockId, int saleId, int saleNo, String saleDate, int saleVocherNo, String Distributer)
    {
        this.CustomerId = customerId;
        this.DealerId = dealerId;
        this.StockId = stockId;
        this.SaleId = saleId;
        this.SaleNo = saleNo;
        this.SaleDate = saleDate;
        this.SaleVocherNo = saleVocherNo;
        this.DistributerName = Distributer;
        this.SaleHeaderInfo =  "شماره فاکتور : " + this.SaleNo +"     "+
                "-"+
                "     "+"تاریخ فاکتور : " + this.SaleDate;
    }
}
