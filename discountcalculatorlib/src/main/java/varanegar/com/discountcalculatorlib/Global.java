package varanegar.com.discountcalculatorlib;

import java.util.ArrayList;
import java.util.List;

import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;

public class Global {
    public static List<DiscountPrizeViewModel> orderPrize = new ArrayList();
    public static boolean hasChoicePrize = false;
    public static String totallyError = "";
    public static String salesError = "";
    public static String evcId;
}
