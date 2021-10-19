package varanegar.com.discountcalculatorlib.viewmodel;


import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by m.aghajani on 10/5/2015.
 */
public class DiscountCallReturnLineData {

    public String returnLineUniqueId;


    public int customerId;
    public String returnUniqueId;
    public int productId;
    public String productName;
    public String productCode;
    public int isFreeItem;
    public BigDecimal requestTotalQty;
    public BigDecimal requestBigQty;
    public long requestBigQtyId;
    public String requestBigQtyName;
    public BigDecimal requestSmallQty;
    public long requestSmallQtyId;
    public String requestSmallQtyName;
    public BigDecimal totalRequestAmount;
    public BigDecimal totalRequestAdd1Amount;
    public BigDecimal totalRequestAdd2Amount;
    public BigDecimal totalRequestDiscount;
    public BigDecimal totalRequestTax;
    public BigDecimal totalRequestCharge;
    public BigDecimal totalRequestNetAmount;
    public int sortId;
    public int indexInfo;
    public String callUniqueId;
    public BigDecimal weight;
    public long priceId;
    public BigDecimal unitPrice;
    public BigDecimal returnTotalQty;
    public BigDecimal returnBigQty;
    public long returnBigQtyId;
    public String returnBigQtyName;
    public BigDecimal returnSmallQty;
    public long returnSmallQtyId;
    public String returnSmallQtyName;
    public BigDecimal totalReturnAmount;
    public BigDecimal totalReturnAdd1Amount;
    public BigDecimal totalReturnAdd2Amount;
    public BigDecimal totalReturnDiscount;
    public BigDecimal totalReturnTax;
    public BigDecimal totalReturnCharge;
    public BigDecimal totalReturnNetAmount;
    public String comment;
    public int returnProductTypeId;
    public int dealerId;
    public int referenceId;
    public String referenceNo;
    public String referenceDate;
    public BigDecimal referenceQty;
    public UUID returnReasonId;

    public BigDecimal totalReturnDis1;
    public BigDecimal totalReturnDis2;
    public BigDecimal totalReturnDis3;
    public BigDecimal totalReturnAddAmount;
    public BigDecimal totalReturnSupAmount;

    public BigDecimal totalReturnDisOther;
    public BigDecimal totalReturnAddOtherAmount;
    public boolean isPromoLine;

    public UUID ReturnProductTypeId;

    public enum ReturnProductTypeEnum
    {
        UNKNOWN(0,"نامشخص"),
        INTACT (1,"سالم"),
        DEFECTIVE (2,"ضایعاتی");

        private int value;
        private String desc;

        public int value()
        {
            return value;
        }

        public String desc()
        {
            return desc;
        }

        public static ReturnProductTypeEnum getReturnType(int returnProductTypeId){

            switch (returnProductTypeId){

                case 0 :
                    return UNKNOWN;
                case 1 :
                    return INTACT;
                case 2:
                    return DEFECTIVE;
                default:
                    return UNKNOWN;
            }
        }

        public static int getReturnTypeId(String name)
        {
            if(name.equals(UNKNOWN.desc))
                return UNKNOWN.value;
            else if(name.equals(INTACT.desc))
                return INTACT.value;
            else if(name.equals(DEFECTIVE.desc))
                return DEFECTIVE.value;
            else
                return UNKNOWN.value;
        }

        public static String GetDesc(int value)
        {
            switch(value)
            {
                case 0:
                    return ReturnProductTypeEnum.UNKNOWN.desc();
                case 1:
                    return ReturnProductTypeEnum.INTACT.desc();
                case 2:
                    return ReturnProductTypeEnum.DEFECTIVE.desc();
                default:
                    return ReturnProductTypeEnum.UNKNOWN.desc();
            }

        }

        ReturnProductTypeEnum(int value, String desc)
        {
            this.value = value;
            this.desc = desc;
        }

    }

//    public DiscountCallReturnLineData(int customerId, String returnUniqueId, String callUniqueId, int productId, String productName, String productCode, BigDecimal unitPrice
//            , long priceId, BigDecimal referenceQty, int referenceId, String referenceNo, int dealerId, String referenceDate)
//    {
//        this(customerId, returnUniqueId, productId, productName, productCode, 0, BigDecimal.ZERO, BigDecimal.ZERO, 0, "", BigDecimal.ZERO, 0, ""
//                , BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, callUniqueId
//                , BigDecimal.ZERO, priceId, unitPrice, BigDecimal.ZERO, BigDecimal.ZERO, 0, "", BigDecimal.ZERO, 0, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
//                , BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "", ReturnProductTypeEnum.UNKNOWN.value(), referenceId, referenceNo, dealerId, referenceDate, referenceQty, null
//                , BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
//    }

//    public DiscountCallReturnLineData(int customerId, String returnUniqueId, int productId, String productName, String productCode, int isFreeItem, BigDecimal requestTotalQty
//            , BigDecimal requestBigQty, int requestBigQtyId, String requestBigQtyName, BigDecimal requestSmallQty , int requestSmallQtyId, String requestSmallQtyName, BigDecimal totalRequestAmount
//            , BigDecimal totalRequestAdd1Amount, BigDecimal totalRequestAdd2Amount, BigDecimal totalRequestDiscount, BigDecimal totalRequestTax, BigDecimal totalRequestCharge
//            , BigDecimal totalRequestNetAmount, int sortId, int indexInfo, String callUniqueId, BigDecimal weight, long priceId, BigDecimal unitPrice, BigDecimal returnTotalQty
//            , BigDecimal returnBigQty, int returnBigQtyId, String returnBigQtyName, BigDecimal returnSmallQty, int returnSmallQtyId, String returnSmallQtyName
//            , BigDecimal totalReturnAmount, BigDecimal totalReturnAdd1Amount, BigDecimal totalReturnAdd2Amount, BigDecimal totalReturnDiscount, BigDecimal totalReturnTax
//            , BigDecimal totalReturnCharge, BigDecimal totalReturnNetAmount, String comment, int returnProductTypeId, int referenceId, String referenceNo, int dealerId, String referenceDate, BigDecimal referenceQty
//            , UUID returnReasonId, BigDecimal totalReturnDis1, BigDecimal totalReturnDis2, BigDecimal totalReturnDis3, BigDecimal totalReturnAddAmount, BigDecimal totalReturnSupAmount)
//    {
//        this.returnLineUniqueId = UUID.randomUUID().toString();
//
//        this.customerId = customerId;
//        this.returnUniqueId = returnUniqueId;
//        this.productId = productId;
//        this.productName = productName;
//        this.productCode = productCode;
//        this.isFreeItem = isFreeItem;
//        this.requestTotalQty = requestTotalQty;
//        this.requestBigQty = requestBigQty;
//        this.requestBigQtyId = requestBigQtyId;
//        this.requestBigQtyName = requestBigQtyName;
//        this.requestSmallQty = requestSmallQty;
//        this.requestSmallQtyId = requestSmallQtyId;
//        this.requestSmallQtyName = requestSmallQtyName;
//        this.totalRequestAmount = totalRequestAmount;
//        this.totalRequestAdd1Amount = totalRequestAdd1Amount;
//        this.totalRequestAdd2Amount = totalRequestAdd2Amount;
//        this.totalRequestDiscount = totalRequestDiscount;
//        this.totalRequestTax = totalRequestTax;
//        this.totalRequestCharge = totalRequestCharge;
//        this.totalRequestNetAmount = totalRequestNetAmount;
//        this.sortId = sortId;
//        this.indexInfo = indexInfo;
//        this.callUniqueId = callUniqueId;
//        this.weight = weight;
//        this.priceId = priceId;
//        this.unitPrice = unitPrice;
//        this.returnTotalQty = returnTotalQty;
//        this.returnBigQty = returnBigQty;
//        this.returnBigQtyId = returnBigQtyId;
//        this.returnBigQtyName = returnBigQtyName;
//        this.returnSmallQty = returnSmallQty;
//        this.returnSmallQtyId = returnSmallQtyId;
//        this.returnSmallQtyName = returnSmallQtyName;
//        this.totalReturnAmount = totalReturnAmount;
//        this.totalReturnAdd1Amount = totalReturnAdd1Amount;
//        this.totalReturnAdd2Amount = totalReturnAdd2Amount;
//        this.totalReturnDiscount = totalReturnDiscount;
//        this.totalReturnTax = totalReturnTax;
//        this.totalReturnCharge = totalReturnCharge;
//        this.totalReturnNetAmount = totalReturnNetAmount;
//        this.comment = comment;
//        this.returnProductTypeId = returnProductTypeId;
//        this.referenceId = referenceId;
//        this.dealerId = dealerId;
//        this.referenceNo = referenceNo;
//        this.referenceDate = referenceDate;
//        this.referenceQty = referenceQty;
//        this.returnReasonId = returnReasonId;
//
//        this.totalReturnDis1 = totalReturnDis1;
//        this.totalReturnDis2 = totalReturnDis2;
//        this.totalReturnDis3 = totalReturnDis3;
//        this.totalReturnAddAmount = totalReturnAddAmount;
//        this.totalReturnSupAmount = totalReturnSupAmount;
//    }
//
//    public void calcValues()
//    {
//        if(this.isFreeItem == 1)
//        {
//            this.totalReturnAmount = BigDecimal.ZERO;
//            this.totalReturnNetAmount = BigDecimal.ZERO;
//            this.totalReturnDiscount = BigDecimal.ZERO;
//            this.totalReturnDis1 = BigDecimal.ZERO;
//            this.totalReturnDis2 = BigDecimal.ZERO;
//            this.totalReturnDis3 = BigDecimal.ZERO;
//            this.totalReturnAddAmount = BigDecimal.ZERO;
//            this.totalReturnAdd1Amount = BigDecimal.ZERO;
//            this.totalReturnAdd2Amount = BigDecimal.ZERO;
//            this.totalReturnTax = BigDecimal.ZERO;
//            this.totalReturnCharge = BigDecimal.ZERO;
//        }
//        else
//        {
//            this.totalReturnAmount = returnTotalQty.multiply(unitPrice);
//            this.totalReturnNetAmount = totalReturnAmount.add(totalReturnTax).add(totalReturnCharge).add(totalReturnAddAmount).subtract(totalReturnDiscount);
//        }
//    }
//
//
//    public DiscountCallReturnLineData getCopy()
//    {
//        DiscountCallReturnLineData copyLine = new DiscountCallReturnLineData();
//
//        copyLine.returnLineUniqueId = this.returnLineUniqueId;
//
//        copyLine.customerId = customerId;
//        copyLine.returnUniqueId = returnUniqueId;
//        copyLine.productId = productId;
//        copyLine.productName = productName;
//        copyLine.productCode = productCode;
//        copyLine.isFreeItem = isFreeItem;
//        copyLine.requestTotalQty = requestTotalQty;
//        copyLine.requestBigQty = requestBigQty;
//        copyLine.requestBigQtyId = requestBigQtyId;
//        copyLine.requestBigQtyName = requestBigQtyName;
//        copyLine.requestSmallQty = requestSmallQty;
//        copyLine.requestSmallQtyId = requestSmallQtyId;
//        copyLine.requestSmallQtyName = requestSmallQtyName;
//        copyLine.totalRequestAmount = totalRequestAmount;
//        copyLine.totalRequestAdd1Amount = totalRequestAdd1Amount;
//        copyLine.totalRequestAdd2Amount = totalRequestAdd2Amount;
//        copyLine.totalRequestDiscount = totalRequestDiscount;
//        copyLine.totalRequestTax = totalRequestTax;
//        copyLine.totalRequestCharge = totalRequestCharge;
//        copyLine.totalRequestNetAmount = totalRequestNetAmount;
//        copyLine.sortId = sortId;
//        copyLine.indexInfo = indexInfo;
//        copyLine.callUniqueId = callUniqueId;
//        copyLine.weight = weight;
//        copyLine.priceId = priceId;
//        copyLine.unitPrice = unitPrice;
//        copyLine.returnTotalQty = returnTotalQty;
//        copyLine.returnBigQty = returnBigQty;
//        copyLine.returnBigQtyId = returnBigQtyId;
//        copyLine.returnBigQtyName = returnBigQtyName;
//        copyLine.returnSmallQty = returnSmallQty;
//        copyLine.returnSmallQtyId = returnSmallQtyId;
//        copyLine.returnSmallQtyName = returnSmallQtyName;
//        copyLine.totalReturnAmount = totalReturnAmount;
//        copyLine.totalReturnAdd1Amount = totalReturnAdd1Amount;
//        copyLine.totalReturnAdd2Amount = totalReturnAdd2Amount;
//        copyLine.totalReturnDiscount = totalReturnDiscount;
//        copyLine.totalReturnTax = totalReturnTax;
//        copyLine.totalReturnCharge = totalReturnCharge;
//        copyLine.totalReturnNetAmount = totalReturnNetAmount;
//        copyLine.comment = comment;
//        copyLine.returnProductTypeId = returnProductTypeId;
//        copyLine.referenceId = referenceId;
//        copyLine.referenceNo = referenceNo;
//        copyLine.dealerId= dealerId;
//        copyLine.referenceDate = referenceDate;
//        copyLine.referenceQty = referenceQty;
//        copyLine.returnReasonId = returnReasonId;
//
//        copyLine.totalReturnDis1 = totalReturnDis1;
//        copyLine.totalReturnDis2 = totalReturnDis2;
//        copyLine.totalReturnDis3 = totalReturnDis3;
//        copyLine.totalReturnAddAmount = totalReturnAddAmount;
//        copyLine.totalReturnSupAmount = totalReturnSupAmount;
//
//        return copyLine;
//    }
}
