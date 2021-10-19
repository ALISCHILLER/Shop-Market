package com.varanegar.vaslibrary.model.SalesmanReportView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;

/**
 * Created by s.foroughi on 11/02/2017.
 */
@Table
public class SalesmanReportViewModel extends BaseModel {

    @Column
    public BigDecimal Cash;
    @Column
    public BigDecimal Cheque;
    @Column
    public BigDecimal POS;
    @Column
    public BigDecimal TotalReturnAmount;
    @Column
    public BigDecimal TotalOrderNetAmount;
    @Column
    public BigDecimal TotalOrderDiscount;
    @Column
    public BigDecimal Reciept;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;

}
