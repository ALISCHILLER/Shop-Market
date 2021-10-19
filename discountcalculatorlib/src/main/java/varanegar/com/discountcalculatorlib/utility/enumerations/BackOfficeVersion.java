package varanegar.com.discountcalculatorlib.utility.enumerations;

public enum BackOfficeVersion {
    SDS16 (16,"SDS.v16"),
    SDS19 (19,"SDS.v19");
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

    public static BackOfficeVersion getBackOfficeVersion(int value)
    {
        switch(value)
        {
            case 16:
                return BackOfficeVersion.SDS16;
            default:
                return BackOfficeVersion.SDS19;
        }

    }

    BackOfficeVersion(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }
}

