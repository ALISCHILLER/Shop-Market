package com.varanegar.vaslibrary.model.paymentTypeOrder;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class PaymentTypeOrderModel extends BaseModel {
    @Column
    public String BackOfficeId;
    @Column
    public String MiddlewareId;
    @Column
    public String PaymentTypeOrderName;
    @Column
    public boolean CheckCredit;
    @Column
    public boolean CheckDebit;
    @Column
    public int PaymentDeadLine;
    @Column
    public int PaymentTime;
    @Column
    public int ForceCash;
    @Column
    public int AllowReceipt;
    @Column
    public UUID PaymentTypeOrderGroupUniqueId;
    @Column
    public String PaymentTypeOrderGroupName;
    @Column
    public Integer GroupBackOfficeId;
    @Column
    public long Code;

    @Override
    public String toString() {
        return PaymentTypeOrderName;
    }
}
