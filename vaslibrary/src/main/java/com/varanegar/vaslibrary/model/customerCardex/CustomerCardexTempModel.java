package com.varanegar.vaslibrary.model.customerCardex;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.network.gson.typeadapters.NestleDateDeSerializer;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/18/2018.
 */
@Table
public class CustomerCardexTempModel extends BaseModel {
    @Column
    @NotNull
    public UUID CustomerUniqueId;
    @Column
    @SerializedName("rowId")
    public int SortId;
    @Column
    public String Type;
    @Column
    public String VoucherTypeName;
    @Column
    public String VoucherNo;
    @Column
    public java.util.Date Date;
    @Column
    public String VoucherDate;
    @Column
    public Currency BedAmount;
    @Column
    public Currency BesAmount;
    @Column
    public Currency RemainAmount;
    @Column
    public String NotDueDate;
    @Column
    @JsonAdapter(NestleDateDeSerializer.class)
    public Date NotDueDateMiladi;
    @Column
    public String BankName;
    @Column
    public String ChqDate;
}
