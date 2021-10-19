package varanegar.com.vdmclient.call;

import java.math.BigDecimal;

/**
 * Created by A.Jafarzadeh on 3/16/2019.
 */

public class OrderHdr extends BaseCallDataModel {
    public int Id;
    public int AccYear;
    public int OrderNo;
    public int OrderType;
    public int CashAmount;
    public BigDecimal Dis1;
    public BigDecimal Dis2;
    public BigDecimal Dis3;
    public BigDecimal Add1;
    public BigDecimal Add2;
}