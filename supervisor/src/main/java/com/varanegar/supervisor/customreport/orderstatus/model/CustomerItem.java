package com.varanegar.supervisor.customreport.orderstatus.model;

import com.google.gson.annotations.SerializedName;

public class CustomerItem {
    @SerializedName("dealerCode")
    private String dealerCode;
    @SerializedName("customerName")
    private String customerName;
    @SerializedName("customerCode")
    private String customerCode;
    @SerializedName("orderWeight")
    private Double orderWeight;
    @SerializedName("pendingOrderWeight")
    private Double pendingOrderWeight;
    @SerializedName("inProgressOrderWeight")
    private Double inProgressOrderWeight;
    @SerializedName("undeliverdOrderWeight")
    private Double undeliverdOrderWeight;
    @SerializedName("finalWeight")
    private Double finalWeight;

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

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

    public Double getOrderWeight() {
        return orderWeight;
    }

    public void setOrderWeight(Double orderWeight) {
        this.orderWeight = orderWeight;
    }

    public Double getPendingOrderWeight() {
        return pendingOrderWeight;
    }

    public void setPendingOrderWeight(Double pendingOrderWeight) {
        this.pendingOrderWeight = pendingOrderWeight;
    }

    public Double getInProgressOrderWeight() {
        return inProgressOrderWeight;
    }

    public void setInProgressOrderWeight(Double inProgressOrderWeight) {
        this.inProgressOrderWeight = inProgressOrderWeight;
    }

    public Double getUndeliverdOrderWeight() {
        return undeliverdOrderWeight;
    }

    public void setUndeliverdOrderWeight(Double undeliverdOrderWeight) {
        this.undeliverdOrderWeight = undeliverdOrderWeight;
    }

    public Double getFinalWeight() {
        return finalWeight;
    }

    public void setFinalWeight(Double finalWeight) {
        this.finalWeight = finalWeight;
    }
}
