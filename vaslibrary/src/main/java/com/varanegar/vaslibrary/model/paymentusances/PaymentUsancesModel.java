package com.varanegar.vaslibrary.model.paymentusances;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */
@Table
public class PaymentUsancesModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public String Title;
    @Column
    public int BuyTypeId;
    @Column
    public int DeferTo;
    @Column
    public int ClearTo;
    @Column
    public int Status;
    @Column
    public boolean IsCash;
    @Column
    public Date ModifiedDateBeforeSend;
    @Column
    public int UserRefBeforeSend;
    @Column
    public boolean AdvanceControl;
}
