package varanegar.com.discountcalculatorlib.utility.enumerations;


public enum BackOfficeType {
    VARANEGAR (1,"ورانگر"),
    RASTAK (2,"رستاک");

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

    public static BackOfficeType getBackOfficeType(int id){

        switch (id){

            case 1 :
                return VARANEGAR;
            default:
                return RASTAK;
        }
    }

    public static String GetDesc(int value)
    {
        switch(value)
        {
            case 1:
                return BackOfficeType.VARANEGAR.desc();
            default:
                return BackOfficeType.RASTAK.desc();
        }

    }

    BackOfficeType(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }}
