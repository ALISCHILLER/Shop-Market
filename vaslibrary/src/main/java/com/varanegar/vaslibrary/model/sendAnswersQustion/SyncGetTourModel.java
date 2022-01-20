package com.varanegar.vaslibrary.model.sendAnswersQustion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SyncGetTourModel {
    private UUID TourUniqueId;
    private Date SendDate;
    private String ApkVersion;
    private List<SyncGetCustomerCallModel> CustomerCalls = new ArrayList<>();

    public UUID getTourUniqueId() {
        return TourUniqueId;
    }

    public void setTourUniqueId(UUID tourUniqueId) {
        TourUniqueId = tourUniqueId;
    }

    public Date getSendDate() {
        return SendDate;
    }

    public void setSendDate(Date sendDate) {
        SendDate = sendDate;
    }

    public String getApkVersion() {
        return ApkVersion;
    }

    public void setApkVersion(String apkVersion) {
        ApkVersion = apkVersion;
    }

    public List<SyncGetCustomerCallModel> getCustomerCalls() {
        return CustomerCalls;
    }

    public void setCustomerCalls(List<SyncGetCustomerCallModel> customerCalls) {
        CustomerCalls = customerCalls;
    }
}
