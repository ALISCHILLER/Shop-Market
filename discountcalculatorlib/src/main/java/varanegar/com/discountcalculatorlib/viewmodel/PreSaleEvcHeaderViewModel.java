package varanegar.com.discountcalculatorlib.viewmodel;

import com.varanegar.processor.annotations.Column;

import java.util.List;

/**
 * Created by Ahad Jafarzadeh on 9/21/2020.
 */

public class PreSaleEvcHeaderViewModel {
    public int OrderRef;
    public String CustRef;
    public int OrderTypeRef;
    public int SaleOfficeRef;
    public String OrderDate;
    public int DealerRef;
    public int BuyTypeRef;
    public int DisType;
    public String PaymentUsanceRef;
    public int EvcType;
    public int RefId;
    public String DocPDate;
    public String SalePDate;
    public String ZTERM;
    public List<DiscountCallOrderLineDataOnline> PreSaleEvcDetails;
    public List<DiscountOrderPrizeViewModel> OrderPrizeList;
    public List<Integer> SelIds;
}
