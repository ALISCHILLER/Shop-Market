package com.varanegar.vaslibrary.model.sendAnswersQustion;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class SyncGetCustomerCallModel {
    private UUID customerUniqueId;
    private Date callDate;
    private String callPDate;
    private Date startTime;
    private String startPTime;
    private Date endTime;
    private String endPTime;
    private double latitude;
    private double longitude;
    private Date receiveDate;
    private String receivePDate;
    private int visitDuration;
    private ArrayList<SyncCustomerCallQuestionnaire> customerCallQuestionnaires;

    public UUID getCustomerUniqueId() {
        return customerUniqueId;
    }

    public void setCustomerUniqueId(UUID customerUniqueId) {
        this.customerUniqueId = customerUniqueId;
    }

    public Date getCallDate() {
        return callDate;
    }

    public void setCallDate(Date callDate) {
        this.callDate = callDate;
    }

    public String getCallPDate() {
        return callPDate;
    }

    public void setCallPDate(String callPDate) {
        this.callPDate = callPDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getStartPTime() {
        return startPTime;
    }

    public void setStartPTime(String startPTime) {
        this.startPTime = startPTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getEndPTime() {
        return endPTime;
    }

    public void setEndPTime(String endPTime) {
        this.endPTime = endPTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getReceivePDate() {
        return receivePDate;
    }

    public void setReceivePDate(String receivePDate) {
        this.receivePDate = receivePDate;
    }

    public int getVisitDuration() {
        return visitDuration;
    }

    public void setVisitDuration(int visitDuration) {
        this.visitDuration = visitDuration;
    }

    public ArrayList<SyncCustomerCallQuestionnaire> getCustomerCallQuestionnaires() {
        return customerCallQuestionnaires;
    }

    public void setCustomerCallQuestionnaires(ArrayList<SyncCustomerCallQuestionnaire> customerCallQuestionnaires) {
        this.customerCallQuestionnaires = customerCallQuestionnaires;
    }
}
