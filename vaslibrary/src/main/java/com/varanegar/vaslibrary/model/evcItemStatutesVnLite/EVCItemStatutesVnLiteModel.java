package com.varanegar.vaslibrary.model.evcItemStatutesVnLite;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;


/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class EVCItemStatutesVnLiteModel extends BaseModel {
    @Column
    public UUID ProducUniqueId;
    @Column
    public String DisId;
    @Column
    public int DisGroup;
    @Column
    public Currency DisAmount;
    @Column
    public Currency AddAmount;
    @Column
    public String EVCId;
    @Column
    public int EVCDetailRowOrder;
}
