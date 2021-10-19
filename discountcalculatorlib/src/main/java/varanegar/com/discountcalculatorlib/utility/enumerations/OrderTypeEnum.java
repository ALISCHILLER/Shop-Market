package varanegar.com.discountcalculatorlib.utility.enumerations;


public enum OrderTypeEnum {
    ORDER (1,"حواله"),
    INVOICE (2,"فاکتور"),
    PRESALE(3,"پیش فاکتور");

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

    public static OrderTypeEnum getOrderType(int orderTypeId){

        switch (orderTypeId){

            case 1 :
                return ORDER;
            case 2 :
                return INVOICE;
            case 3 :
                return PRESALE;
            default:
                return ORDER;
        }
    }

    public static String GetDesc(int value)
    {
        switch(value)
        {
            case 1:
                return OrderTypeEnum.ORDER.desc();
            case 2:
                return OrderTypeEnum.INVOICE.desc();
            case 3:
                return OrderTypeEnum.PRESALE.desc();
            default:
                return OrderTypeEnum.ORDER.desc();
        }

    }

    OrderTypeEnum(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }
}
