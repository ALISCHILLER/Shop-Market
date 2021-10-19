package varanegar.com.vdmclient.call;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by A.Razavi on 3/13/2019.
 */

public class EvcHeader {
    public Integer Id;
    public Integer CustRef;
    public BigDecimal TotalAmount;
    public BigDecimal Dis1;
    public BigDecimal Dis2;
    public BigDecimal Dis3;
    public BigDecimal Add1;
    public BigDecimal Add2;
    public BigDecimal Tax;
    public BigDecimal Charge;
    public BigDecimal OtherDiscount;
    public BigDecimal OtherAddition;
    public Integer OrderType;
    public String PayType;
    public String CallId;
    public Integer RefId;
    public String EvcId;
    //public Entity.EvcType EvcType;
    public Integer EvcTypeValue;
    //public Entity.DiscountType DisType;
    public Integer DiscountTypeValue;
    public Integer DcRef;
    public Integer DcSaleOfficeRef;
    public Integer SaleOfficeRef;
    public String DcCode;
    public String DateOf;
    public String OprDate;
    public String EvcDate;
    public Integer AccYear;
    public Integer StockDcRef;
    public String StockDcCode ;
    public Integer DealerRef ;
    public String DealerCode ;
    public Integer SupervisorRef ;
    public String SupervisorCode;
    public String PaymentUsanceRef ;
    public Integer TOrderRef ;
    public Integer TOrderNo ;
    public String SalesGoodsDetailXML ;
    public String SalesGoodsGroupTreeXML ;
    public String SalesGoodsMainSubTypeDetailXML ;
    public String SalesCustomerMainSubTypeDetailXML ;
    public Integer BuyTypeRef ;
    public Integer UsanceDay ;
    public Integer SaleRef ;
    public Integer OrderRef ;
    public Integer OrderNo ;
    public String DCName ;
    public String PayTypeName ;
    public String DisTypeName ;
    public String OrderTypeName ;
    public String StockDCName ;
    public String SaleOfficeName ;
    public String PaymentUsanceName ;
    public String SupervisorName ;
}
