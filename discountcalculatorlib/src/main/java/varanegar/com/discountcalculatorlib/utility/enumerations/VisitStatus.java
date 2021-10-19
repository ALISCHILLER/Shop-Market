package varanegar.com.discountcalculatorlib.utility.enumerations;

public enum VisitStatus {
    UNKNOWN (1,"نامشخص"),
    NOT_VISITED (2,"عدم ویزیت"),
    VISITED_DISTED(3,"توزیع شده"),
    VISITED_REJECTED(4,"عدم توزیع");

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
                return VisitStatus.UNKNOWN.desc();
            case 2:
                return VisitStatus.NOT_VISITED.desc();
            case 3:
                return VisitStatus.VISITED_DISTED.desc();
            case 4:
                return VisitStatus.VISITED_REJECTED.desc();
            default:
                return VisitStatus.UNKNOWN.desc();
        }

    }

    public static String GetDesc(String value)
    {
        if(value.equals(UNKNOWN.toString()))
            return UNKNOWN.desc();
        else if(value.equals(NOT_VISITED.toString()))
            return NOT_VISITED.desc();
        else if(value.equals( VISITED_DISTED.toString()))
            return VISITED_DISTED.desc();
        else if(value.equals(VISITED_REJECTED.toString()))
            return VISITED_REJECTED.desc();
        else
            return UNKNOWN.desc();
    }

    public static VisitStatus getVisitStatus(String status)
    {
        if(status.equals(UNKNOWN.desc()))
            return UNKNOWN;
        else if(status.equals(NOT_VISITED.desc()))
            return NOT_VISITED;
        else if(status.equals(VISITED_DISTED.desc()))
            return VISITED_DISTED;
        else if(status.equals(VISITED_REJECTED.desc()))
            return VISITED_REJECTED;
        else
            return UNKNOWN;

    }

    VisitStatus(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }
}
