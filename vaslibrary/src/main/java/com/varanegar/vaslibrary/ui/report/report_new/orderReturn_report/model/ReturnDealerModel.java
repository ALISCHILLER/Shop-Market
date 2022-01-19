package com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReturnDealerModel {
    @SerializedName("dealerName")
    private String dealerName;
    @SerializedName("dealerCode")
    private String dealerCode;
    @SerializedName("productCountCa")
    private Double productCountCa;

    @SerializedName("dealersItems")
    private List<ReturnCustomerModel> customerModels;

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public Double getProductCountCa() {
        return productCountCa;
    }

    public void setProductCountCa(Double productCountCa) {
        this.productCountCa = productCountCa;
    }

    public List<ReturnCustomerModel> getCustomerModels() {
        return customerModels;
    }

    public void setCustomerModels(List<ReturnCustomerModel> customerModels) {
        this.customerModels = customerModels;
    }
}
