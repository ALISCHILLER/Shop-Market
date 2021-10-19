package com.varanegar.vaslibrary.model.CustomerOpenInvoicesView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 25/01/2017.
 */

@Table
public class CustomerOpenInvoicesViewModel extends BaseModel {

    @Column
    @NotNull
    public UUID CustomerId;
    @Column
    public int SaleNo;
    @Column
    public String SalePDate;
    @Column
    public Currency TotalAmount;
    @Column
    public Currency PayAmount;
    @Column
    public Currency RemAmount;
    @Column
    public String DealerName;
    @Column
    public UUID DealerId;

}
