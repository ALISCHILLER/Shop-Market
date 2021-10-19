package com.varanegar.vaslibrary.model.customerCallOrderLinesItemStatutes;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.util.UUID;

/**
 * Created by s.foroughi on 07/02/2017.
 */

@Table
public class CustomerCallOrderLinesItemStatutesModel extends BaseModel {

    @Column
    @NotNull
    public UUID ProductId;
    @Column
    public int RowOrder;
    @Column
    public int DisRef;
    @Column
    public int DisGroup;
    @Column
    public Currency AddAmount;
    @Column
    public Currency SupAmount;
    @Column
    public Currency Discount;
    @Column
    public String EvcId;

}
