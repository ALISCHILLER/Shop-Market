package com.varanegar.supervisor.customreport.orderreturn.model;

import com.google.gson.annotations.SerializedName;

public class ReturnReasonModel {
    @SerializedName("reason")
    private String reason;
    @SerializedName("reasonCode")
    private String reasonCode;
    @SerializedName("productCountCa")
    private Double productCountCa;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Double getProductCountCa() {
        return productCountCa;
    }

    public void setProductCountCa(Double productCountCa) {
        this.productCountCa = productCountCa;
    }
}
