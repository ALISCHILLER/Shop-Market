package com.varanegar.supervisor.customreport.orderstatus.model;

import java.util.List;

public class OrderStatusReportFlat {
    private int level;

    private String date;
    private Double orderWeight;
    private Double pendingOrderWeight;
    private Double inProgressOrderWeight;
    private Double undeliverdOrderWeight;
    private Double finalWeight;
    private String dealerName;
    private String dealerCode;
    private Double deliverdOrderWeight;
    private String customerName;
    private String customerCode;

    private List<OrderStatusReportFlat> childs;

    public OrderStatusReportFlat(int level,
                                 String date,
                                 Double orderWeight,
                                 Double pendingOrderWeight,
                                 Double inProgressOrderWeight,
                                 Double undeliverdOrderWeight,
                                 Double finalWeight,
                                 String dealerName,
                                 String dealerCode,
                                 Double deliverdOrderWeight,
                                 String customerName,
                                 String customerCode) {
        this.level = level;
        this.date = date;
        this.orderWeight = orderWeight;
        this.pendingOrderWeight = pendingOrderWeight;
        this.inProgressOrderWeight = inProgressOrderWeight;
        this.undeliverdOrderWeight = undeliverdOrderWeight;
        this.finalWeight = finalWeight;
        this.dealerName = dealerName;
        this.dealerCode = dealerCode;
        this.deliverdOrderWeight = deliverdOrderWeight;
        this.customerName = customerName;
        this.customerCode = customerCode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

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

    public Double getDeliverdOrderWeight() {
        return deliverdOrderWeight;
    }

    public void setDeliverdOrderWeight(Double deliverdOrderWeight) {
        this.deliverdOrderWeight = deliverdOrderWeight;
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

    public List<OrderStatusReportFlat> getChilds() {
        return childs;
    }

    public void setChilds(List<OrderStatusReportFlat> childs) {
        this.childs = childs;
    }
}
