package com.varanegar.supervisor.customreport.orderreturn.model;

import java.util.List;

public class ReturnReportFlat {
    private String dealerName;
    private String dealerCode;
    private Double productCountCa;
    private String customerName;
    private String customerCode;
    private String reason;
    private String reasonCode;

    private List<ReturnReportFlat> childs;

    public ReturnReportFlat(
            String dealerName,
            String dealerCode,
            Double productCountCa,
            String customerName,
            String customerCode,
            String reason,
            String reasonCode) {
        this.dealerName = dealerName;
        this.dealerCode = dealerCode;
        this.productCountCa = productCountCa;
        this.customerName = customerName;
        this.customerCode = customerCode;
        this.reason = reason;
        this.reasonCode = reasonCode;
    }

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

    public List<ReturnReportFlat> getChilds() {
        return childs;
    }

    public void setChilds(List<ReturnReportFlat> childs) {
        this.childs = childs;
    }
}
