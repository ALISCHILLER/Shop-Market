package com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderStatusReport {
    @SerializedName("date")
    private String date;
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
    private List<DealersItem> dealersItems;

    public List<DealersItem> getDealersItems() {
        return dealersItems;
    }

    public void setDealersItems(List<DealersItem> dealersItems) {
        this.dealersItems = dealersItems;
    }

    private boolean isExpand;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
