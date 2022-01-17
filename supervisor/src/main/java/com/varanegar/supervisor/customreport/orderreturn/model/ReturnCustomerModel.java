package com.varanegar.supervisor.customreport.orderreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReturnCustomerModel {
    @SerializedName("CustomerName")
    private String customerName;
    @SerializedName("CustomerCode")
    private String customerCode;
    @SerializedName("ProductCountCa")
    private Double productCountCa;

    @SerializedName("CustomerItems")
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
