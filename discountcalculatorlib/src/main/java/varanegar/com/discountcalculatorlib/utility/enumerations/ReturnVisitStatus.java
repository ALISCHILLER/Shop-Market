package varanegar.com.discountcalculatorlib.utility.enumerations;

public enum ReturnVisitStatus
{
    UNKNOWN (1,"نامشخص"),
    RETURN_CANCELED (2,"انصراف"),
    RETURNED (3,"برگشتی");

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

    public static String GetDesc(int value)
    {
        switch(value)
        {
            case 1:
                return ReturnVisitStatus.UNKNOWN.desc();
            case 2:
                return ReturnVisitStatus.RETURN_CANCELED.desc();
            case 3:
                return ReturnVisitStatus.RETURNED.desc();
            default:
                return ReturnVisitStatus.UNKNOWN.desc();
        }

    }

    ReturnVisitStatus(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }
}
