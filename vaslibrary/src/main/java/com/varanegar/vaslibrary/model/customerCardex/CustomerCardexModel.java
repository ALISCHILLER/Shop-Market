package com.varanegar.vaslibrary.model.customerCardex;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.projection.OperatorProjection;
import com.varanegar.framework.network.gson.typeadapters.NestleDateDeSerializer;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 9/11/2017.
 */
@Table
public class CustomerCardexModel extends BaseModel
{
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
    public Date Date;
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

    public CustomerCardexTempModel convert() {
        CustomerCardexTempModel customerCardexTempModel = new CustomerCardexTempModel();
        customerCardexTempModel.UniqueId = UniqueId;
        customerCardexTempModel.CustomerUniqueId = CustomerUniqueId;
        customerCardexTempModel.SortId = SortId;
        customerCardexTempModel.Type = Type;
        customerCardexTempModel.VoucherTypeName = VoucherTypeName;
        customerCardexTempModel.VoucherNo = VoucherNo;
        customerCardexTempModel.Date = Date;
        customerCardexTempModel.VoucherDate = VoucherDate;
        customerCardexTempModel.BedAmount = BedAmount;
        customerCardexTempModel.BesAmount = BesAmount;
        customerCardexTempModel.RemainAmount = RemainAmount;
        customerCardexTempModel.NotDueDate = NotDueDate;
        customerCardexTempModel.NotDueDateMiladi = NotDueDateMiladi;
        customerCardexTempModel.BankName = BankName;
        customerCardexTempModel.ChqDate = ChqDate;
        return customerCardexTempModel;
    }

}
