package com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderStatusModel {
    @SerializedName("DealersId")
   private List<String> dealersId;
    @SerializedName("StartDate")
   private String startdata;
    @SerializedName("EndDate")
   private  String endDate;

    public List<String> getDealersId() {
        return dealersId;
    }

    public void setDealersId(List<String> dealersId) {
        this.dealersId = dealersId;
    }

    public String getStartdata() {
        return startdata;
    }

    public void setStartdata(String startdata) {
        this.startdata = startdata;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
