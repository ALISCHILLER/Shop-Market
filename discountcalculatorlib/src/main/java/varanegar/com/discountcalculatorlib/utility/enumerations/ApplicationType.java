package varanegar.com.discountcalculatorlib.utility.enumerations;


public enum ApplicationType {
    HOTSALE (1,"فروش گرم"),
    PRESALE (2,"پیش ویزیت"),
    DISTRIBUTION(3,"توزیع");

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

    public static ApplicationType getAppType(int id){

        switch (id){

            case 1 :
                return HOTSALE;
            case 2 :
                return PRESALE;
            default:
                return DISTRIBUTION;
        }
    }



    ApplicationType(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }}
