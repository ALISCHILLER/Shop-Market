package com.varanegar.vaslibrary.model.RequestReportView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;

import java.util.UUID;

/**
 * Created by s.foroughi on 12/02/2017.
 */
@Table
public class RequestReportViewModel extends BaseModel {
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public Currency TotalOrderNetAmount;
    @Column
    public String PaymentTypeBaseName;
    @Column
    public boolean HasReturn;
    @Column
    public String CallType;
    @Column
    public String ConfirmStatus;
    @Column
    public String LocalPaperNo;
    @Column
    public String StoreName;
    @Column
    public UUID OrderUniqueId;
    @Column
    public Currency Discount;
}
