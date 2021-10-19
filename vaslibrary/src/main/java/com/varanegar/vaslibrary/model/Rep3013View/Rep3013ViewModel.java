package com.varanegar.vaslibrary.model.Rep3013View;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

/**
 * Created by s.foroughi on 17/01/2017.
 */
@Table
public class Rep3013ViewModel extends BaseModel {


    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public Currency CustRemAmountForSaleOffice;
    @Column
    public Currency CustRemAmountAll;

}
