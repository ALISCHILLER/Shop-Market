package varanegar.com.discountcalculatorlib.utility.enumerations;


public enum ProductType {
    BULK (2,"فله دو واحدی");

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
                return ProductType.BULK.desc();
            default:
                return ProductType.BULK.desc();
        }

    }

    ProductType(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }

}
