package com.varanegar.supervisor.model.reviewreport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.List;


@Table
public class ReviewreportModel extends BaseModel {

    @SerializedName("orderNumber")
    @Expose
    @Column
    public String orderNumber;
    @SerializedName("orderStatus")
    @Expose
    @Column
    public String orderStatus;
    @SerializedName("orderDate")
    @Expose
    @Column
    public String orderDate;
    @SerializedName("dealerCode")
    @Expose
    @Column
    public String dealerCode;
    @SerializedName("dealerName")
    @Expose
    @Column
    public String dealerName;
    @SerializedName("customerCode")
    @Expose
    @Column
    public String customerCode;
    @SerializedName("customerName")
    @Expose
    @Column
    public String customerName;
    @SerializedName("paymentType")
    @Expose
    @Column
    public String paymentType;
    @SerializedName("comment")
    @Expose
    @Column
    public String comment;
    @SerializedName("customerCategory")
    @Expose
    @Column
    public String customerCategory;
    @SerializedName("items")
    @Expose
    public List<ItemsModel> items = null;

}
