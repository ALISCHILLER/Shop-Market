package com.varanegar.supervisor.model.reviewreport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lidroid.xutils.db.annotation.Table;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;

@Table
public class items extends BaseModel {
    @SerializedName("productCategory")
    @Expose
    @Column
    public String productCategory;
    @SerializedName("amount")
    @Expose
    @Column
    public int amount;
    @SerializedName("productCode")
    @Expose
    @Column
    public String productCode;
    @SerializedName("productName")
    @Expose
    @Column
    public String productName;
    @SerializedName("productCount")
    @Expose
    @Column
    public int productCount;
    @SerializedName("productCountStr")
    @Expose
    @Column
    public String productCountStr;
    @SerializedName("tax")
    @Expose
    @Column
    public int tax;



}
