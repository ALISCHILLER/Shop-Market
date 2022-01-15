package com.varanegar.supervisor.customreport.orderstatus.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DealersItem {
    @SerializedName("date")
    private String date;
    @SerializedName("dealerName")
    private String dealerName;
    @SerializedName("dealerCode")
    private String dealerCode;
    @SerializedName("orderWeight")
    private Double orderWeight;
    @SerializedName("pendingOrderWeight")
    private Double pendingOrderWeight;
    @SerializedName("inProgressOrderWeight")
    private Double inProgressOrderWeight;
    @SerializedName("undeliverdOrderWeight")
    private Double   undeliverdOrderWeight;
    @SerializedName("deliverdOrderWeight")
    private Double  deliverdOrderWeight;
    @SerializedName("customerItems")
    List<CustomerItem> customerItems;

    public Double getInProgressOrderWeight() {
        return inProgressOrderWeight;
    }

    public void setInProgressOrderWeight(Double inProgressOrderWeight) {
        this.inProgressOrderWeight = inProgressOrderWeight;
    }

    private boolean isExpand;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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

    public Double getUndeliverdOrderWeight() {
        return undeliverdOrderWeight;
    }

    public void setUndeliverdOrderWeight(Double undeliverdOrderWeight) {
        this.undeliverdOrderWeight = undeliverdOrderWeight;
    }

    public Double getDeliverdOrderWeight() {
        return deliverdOrderWeight;
    }

    public void setDeliverdOrderWeight(Double deliverdOrderWeight) {
        this.deliverdOrderWeight = deliverdOrderWeight;
    }

    public List<CustomerItem> getCustomerItems() {
        return customerItems;
    }

    public void setCustomerItems(List<CustomerItem> customerItems) {
        this.customerItems = customerItems;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
