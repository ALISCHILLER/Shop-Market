package com.varanegar.vaslibrary.model.customerpathview;

import androidx.annotation.ColorInt;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.Length;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Property;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customer.CustomerModel;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */
@Table
public class CustomerPathViewModel extends CustomerModel
{
    @Column
    public UUID VisitTemplatePathId;
    @Column
    public Currency TotalOrderAmount;
    @Column
    public int PathRowId;
    @Column
    public String CallType;
    @Column
    public String CustomerCategoryName;
}
