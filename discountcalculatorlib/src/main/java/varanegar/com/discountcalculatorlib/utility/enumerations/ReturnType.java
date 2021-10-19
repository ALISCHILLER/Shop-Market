package varanegar.com.discountcalculatorlib.utility.enumerations;

/**
 * Created by Emmireco on 06/11/2017.
 */

public enum ReturnType
{
    NOREF(1,"بدون مبنا"),
    WITHREF(2,"با مبنا"),
    NOREF_WITHOUT_ORDER(3,"بدون مبنا بدون درخواست"),
    WITHREF_WITHOUT_ORDER(4,"با مبنا بدون درخواست");

    private int value;
    private String desc;

    public int value() {return value;}

    public String desc()
    {
        switch(value)
        {
            case 1:
                return ReturnType.NOREF.desc;
            case 2:
                return ReturnType.WITHREF.desc;
            case 3:
                return ReturnType.NOREF_WITHOUT_ORDER.desc;
            case 4:
                return ReturnType.WITHREF_WITHOUT_ORDER.desc;
            default:
                return ReturnType.NOREF.desc;
        }
    }

    ReturnType(int value, String desc)
    {
        this.value = value;
        this.desc = desc;
    }

    public static ReturnType getReturnType(int returnTypeId){

        switch (returnTypeId){

            case 1 :
                return NOREF;
            case 2 :
                return WITHREF;
            case 3:
                return NOREF_WITHOUT_ORDER;
            case 4:
                return WITHREF_WITHOUT_ORDER;
            default:
                return null;
        }
    }
}
