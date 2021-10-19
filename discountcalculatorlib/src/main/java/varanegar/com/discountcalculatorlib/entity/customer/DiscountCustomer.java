package varanegar.com.discountcalculatorlib.entity.customer;

import java.math.BigDecimal;
import java.util.Date;

import varanegar.com.discountcalculatorlib.utility.enumerations.VisitStatus;


public class DiscountCustomer {
    public int customerId;
    public int customerCode;
    public String customerName;
    public String CustomerGUID;
    public String phone;
    public String mobile;
    public String address;
    public String storeName;
    public int lat=0;
    public int lng=0;
    public String alarm;
    public int openChequeCount;
    public BigDecimal openChequeAmount;
    public int returnChequeCount;
    public BigDecimal returnChequeAmount;
    public int openInvoiceCount;
    public BigDecimal openInvoiceAmount;
    public BigDecimal remainDebit;
    public BigDecimal remainCredit;
    public BigDecimal CustomerRemain;
    public String EconomicCode;
    public String NationalCode;
    public BigDecimal InitDebit;
    public BigDecimal InitCredit;
    public int totalorder;
    public String visitStatus= "UNKNOWN";
    public String visitStatusReason;
    public int pasajId;
    public int visitlat=0;
    public int visitlng=0;
    public Date gpsSaveDate;
    public int NoSaleReasonId;
    public String ErrorType;
    public String ErrorMessage;
    public int NoSend;
    public int SendDataStatus = 0;
    public int IsInfoSend;
    public int IsInfoEdit;
    public int SortId;
    public int CityId;
    public int StateId = 0;
    public int CustomerLevelId = 0;
    public int CustomerActivityId = 0;
    public int CustomerCategoryId = 0;
    public int CenterId = 0;
    public int ZoneId = 0;
    public int AreaId = 0;

    public int hasCancelOrder;
    public int isPrinted;
    public int ActionGUID = 0;
    public int ActionType = 0;

    public DiscountCustomer()
    {

    }

    public DiscountCustomer(int customerid, int customerCode, String customerName, String phone, String mobile, String address, int lat, int lng,
                    String storeName, String alarm, int openChequeCount, int openChequeAmount, int returnChequeCount,
                    int returnChequeAmount, int openInvoiceCount, int openInvoiceAmount, int remainDebit, int remainCredit, int CustomerRemain,
                    String EconomicCode, String NationalCode, int InitDebit, int InitCredit, int totalorder,
                    String visitStatus, String visitStatusReason, int pasajId, int visitlat, int visitlng, Date gpsSaveDate,
                    int NoSaleReasonId, String ErrorType, String ErrorMessage, int NoSend, int SendDataStatus,
                    int IsInfoSend, int IsInfoEdit, int SortId, int CityId, int StateId, int HasCancelOrder,
                    int CustomerLevelId, int CustomerActivityId, int CustomerCategoryId, int CenterId, int ZoneId, int AreaId,
                    int IsPrinted, int ActionGUID, int ActionType, String customerGUID)

    {
        this.customerId = customerid;
        this.customerCode = customerCode;
        this.customerName= customerName;
        this.CustomerGUID = customerGUID;
        this.phone = phone;
        this.mobile = mobile;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.storeName = storeName;
        this.alarm = alarm;
        this.openChequeCount = openChequeCount;
        this.openChequeAmount  = new BigDecimal(openChequeAmount);
        this.returnChequeCount = returnChequeCount;
        this.returnChequeAmount = new BigDecimal(returnChequeAmount);
        this.openInvoiceCount = openInvoiceCount;
        this.openInvoiceAmount = new BigDecimal(openInvoiceAmount);
        this.remainDebit = new BigDecimal(remainDebit);
        this.remainCredit = new BigDecimal(remainCredit);
        this.CustomerRemain = new BigDecimal(CustomerRemain);
        this.EconomicCode = EconomicCode;
        this.NationalCode = NationalCode;
        this.InitDebit = new BigDecimal(InitDebit);
        this.InitCredit = new BigDecimal(InitCredit);
        this.totalorder = totalorder;
        this.visitStatus = visitStatus;
        if(visitStatus == null)
            this.visitStatus = VisitStatus.UNKNOWN.toString();
        this.visitStatusReason = visitStatusReason;
        this.pasajId = pasajId;
        this.visitlat = visitlat;
        this.visitlng = visitlng;
        this.gpsSaveDate = gpsSaveDate;
        this.NoSaleReasonId = NoSaleReasonId;
        this.ErrorType = ErrorType;
        this.ErrorMessage = ErrorMessage;
        this.NoSend = NoSend;
        this.SendDataStatus = SendDataStatus;
        this. IsInfoSend = IsInfoSend;
        this. IsInfoEdit =IsInfoEdit;
        this. SortId = SortId;
        this. CityId =CityId ;
        this.StateId = StateId;
        this.hasCancelOrder = HasCancelOrder;
        this.CustomerLevelId = CustomerLevelId;
        this.CustomerActivityId = CustomerActivityId;
        this.CustomerCategoryId = CustomerCategoryId;
        this.CenterId = CenterId;
        this.ZoneId = ZoneId;
        this.AreaId = AreaId;
        this.isPrinted = IsPrinted;
        this.ActionGUID = ActionGUID;
        this.ActionType = ActionType;
    }
}