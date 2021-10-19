package com.varanegar.vaslibrary.model.printer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by g.aliakbar on 09/04/2018.
 */
@Table
public class CancelInvoiceModel extends BaseModel {

    @Column
    public UUID TourUniqueId;
    @Column
    public UUID CustomerUniqueId ;
    @Column
    public String Comment;
    @Column
    public double Amount ;
}
