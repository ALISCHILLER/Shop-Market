package varanegar.com.vdmclient.call;

import java.math.BigDecimal;

/**
 * Created by A.Jafarzadeh on 3/16/2019.
 */

public class DisSale extends BaseCallDataModel {
    public int Id;
    public int HdrRef;
    public int ItemRef;
    public int RowNo;
    public int ItemType;
    public int DisRef;
    public int DisGroup;
    public BigDecimal Discount;
    public BigDecimal AddAmount;
}
