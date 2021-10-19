package varanegar.com.discountcalculatorlib.viewmodel;

import java.util.List;

/**
 * Created by A.Razavi on 3/11/2018.
 */

public class DiscountCallOrderDataOnline {
    public String CustRef ;
    public int OrderTypeRef;
    public int SaleOfficeRef;
    public String OrderDate;
    public int DealerRef;
    public int BuyTypeRef;
    public int DisType;
    public String PaymentUsanceRef;
    public int EvcType;
    public int RefId;

    public List<Integer> SelIds;
    public List<DiscountCallOrderLineDataOnline> PreSaleEvcDetails;
}
