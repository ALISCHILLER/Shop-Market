package varanegar.com.discountcalculatorlib.viewmodel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//TODO ASAL
//import retrofit2.Response;

/**
 * Created by A.Jafarzadeh on 7/3/2017.
 */

public class OrderMaster
{
    public OrderMaster()
    {
        OrderLineList = new ArrayList<OrderLine>();
    }

    public int CustomerId;

    public int BuyTypeRef;

    public int DisType;

    public int EvcType;

    public int SaleRef;

    public int PaymentUsanceRef;

    public int OrderTypeId;

    public int SaleOfficeRef;

    public int DealerId;

    public String Date;

    public List<OrderLine> OrderLineList;

    //TODO ASAL public Response response;

    public BigDecimal TotalInvoiceDist1;

    public BigDecimal TotalInvoiceDist2;

    public BigDecimal TotalInvoiceDist3;

}
