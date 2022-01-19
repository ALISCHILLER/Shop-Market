package com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReturnCustomerModel {
    @SerializedName("customerName")
    private String customerName;
    @SerializedName("customerCode")
    private String customerCode;
    @SerializedName("productCountCa")
    private Double productCountCa;

    @SerializedName("customerItems")
    private List<ReturnReasonModel> returnReasonModels;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public Double getProductCountCa() {
        return productCountCa;
    }

    public void setProductCountCa(Double productCountCa) {
        this.productCountCa = productCountCa;
    }

    public List<ReturnReasonModel> getReturnReasonModels() {
        return returnReasonModels;
    }

    public void setReturnReasonModels(List<ReturnReasonModel> returnReasonModels) {
        this.returnReasonModels = returnReasonModels;
    }
}
