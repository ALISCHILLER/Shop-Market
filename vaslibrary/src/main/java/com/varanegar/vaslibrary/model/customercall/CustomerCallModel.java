package com.varanegar.vaslibrary.model.customercall;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/27/2017.
 */
@Table
public class CustomerCallModel extends BaseModel {
    @Column
    @NotNull
    public UUID CustomerId;
    @Column(isEnum = true)
    public CustomerCallType CallType;
    @Column
    @NotNull
    public Date CreatedTime;
    @Column
    @NotNull
    public Date UpdatedTime;
    @Column
    @Nullable
    public String ExtraField1;
    @Column
    @Nullable
    public String ExtraField2;
    @Column
    @Nullable
    public String ExtraField3;
    @Column
    public boolean ConfirmStatus;
}
