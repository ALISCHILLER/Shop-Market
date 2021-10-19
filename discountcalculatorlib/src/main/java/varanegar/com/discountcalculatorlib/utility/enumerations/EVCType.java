package varanegar.com.discountcalculatorlib.utility.enumerations;

public enum EVCType{
    NewTOSELL(0, "پیش فاکتور"),
    TOSELL(1, "تبدیل درخواست به فاکتور یا حواله"),
    SELLRETURN(2, "برگشت فاکتور"),
    FOLLOW_ORDER(3, "پیگیری حواله"),
    HOTSALE(1, "فروش گرم"),
    POS(5, "فروش فرشگاهی");

    private int value;
    private String desc;

    public int value() {return value;}

    EVCType(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }

    public static EVCType getByCode(int code){

        switch (code){

            case 1 :
                return TOSELL;
            case 2 :
                return SELLRETURN;
            case 3 :
                return FOLLOW_ORDER;
            case 4 :
                return HOTSALE;
            default:
                return POS;
        }
    }
}
