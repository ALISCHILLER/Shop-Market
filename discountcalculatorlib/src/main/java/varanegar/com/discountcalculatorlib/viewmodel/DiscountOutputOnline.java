package varanegar.com.discountcalculatorlib.viewmodel;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Razavi on 3/11/2018.
 */

public class DiscountOutputOnline {

    public int errorCode;
    public String message;
    public String paymentUsanceRef;
    public UUID paymentUsanceId;
    public String paymentUsanceName;
    public int usanceDay;
    public double cashDuration;
    public double checkDuration;
    public List<OutputOnlineDetails> items = new ArrayList<>();
    public ArrayList<DiscountEvcPrizeData> discountEvcPrize = new ArrayList<>();
    public List<DiscountPrizeViewModel> orderPrize = new ArrayList();
    public List<DiscountEvcItemStatuteDataOnline> itemStatute = new ArrayList();
}
