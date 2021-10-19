package varanegar.com.discountcalculatorlib.entity.general;


public class DiscountFreeReason
{
    public int freeReasonId;
    public String freeReasonName;
    public int calcPriceType;

    public DiscountFreeReason(int freeReasonId, String freeReasonName, int calcPriceType)
    {
        this.freeReasonId = freeReasonId;
        this.freeReasonName = freeReasonName;
        this.calcPriceType = calcPriceType;
    }

    public DiscountFreeReason()
    {
    }
}

