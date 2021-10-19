package varanegar.com.discountcalculatorlib.utility;

public class DiscountException extends RuntimeException {
    private int code;
    private Object tag;

    public int getCode() {
        return code;
    }
    public Object getTag() {
        return tag;
    }

    public DiscountException(String msg)
    {
        super(msg);
    }
    public DiscountException(int cd, String msg)
    {
        super(msg);
        code = cd;
    }

    public DiscountException(int cd, String msg, Object tg)
    {
        super(msg);
        code = cd;
        tag = tg;
    }
}
