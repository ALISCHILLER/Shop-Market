package com.varanegar.vaslibrary.model.customercallview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 10/10/2017.
 */
@Table
public class CustomerCallViewModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public String CallType;
    @Column
    public String ConfirmStatus;
    @Column
    public int ConfirmCount;
    @Column
    public int TotalCount;
    @Column
    public String Confirmed;
}
