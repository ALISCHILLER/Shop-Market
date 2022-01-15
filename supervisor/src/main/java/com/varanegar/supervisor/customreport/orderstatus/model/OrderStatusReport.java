package com.varanegar.supervisor.customreport.orderstatus.model;

import com.google.gson.annotations.SerializedName;
import com.varanegar.supervisor.customreport.orderstatus.annotations.GridColumn;

import java.util.Date;
import java.util.List;

public class OrderStatusReport {
    @SerializedName("date")
    @GridColumn(width = 50, position = 0)
    private String date;
    @GridColumn(width = 50, position = 0)
    @SerializedName("orderWeight")
    private Double  orderWeight;
    @GridColumn(width = 50, position = 0)
    @SerializedName("pendingOrderWeight")
    private Double   pendingOrderWeight;
    @GridColumn(width = 50, position = 0)
    @SerializedName("inProgressOrderWeight")
    private Double  inProgressOrderWeight;
    @GridColumn(width = 50, position = 0)
    @SerializedName("undeliverdOrderWeight")
    private Double  undeliverdOrderWeight;
    @GridColumn(width = 50, position = 0)
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
