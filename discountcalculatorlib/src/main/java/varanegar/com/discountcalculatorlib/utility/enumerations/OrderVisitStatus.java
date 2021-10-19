package varanegar.com.discountcalculatorlib.utility.enumerations;

public enum OrderVisitStatus
{
    UNKNOWN (1,"نامشخص"),
    NOT_DELIVERED (2,"عدم تحویل"),
    PARTIAL_DELIVERED (3, "تحویل قسمتی"),
    DELIVERED (4,"تحویل کامل");

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
                return OrderVisitStatus.UNKNOWN.desc();
            case 2:
                return OrderVisitStatus.NOT_DELIVERED.desc();
            case 3:
                return OrderVisitStatus.PARTIAL_DELIVERED.desc();
            case 4:
                return OrderVisitStatus.DELIVERED.desc();
            default:
                return OrderVisitStatus.UNKNOWN.desc();
        }

    }

    OrderVisitStatus(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }

}
