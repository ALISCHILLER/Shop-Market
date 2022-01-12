package com.varanegar.vaslibrary.model.sendAnswersQustion;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class SyncGetCustomerCallModel {

    public UUID customerUniqueId;

    public Date callDate;

    public String callPDate;

    public Date startTime;

    public String startPTime;

    public Date endTime;

    public String endPTime;


    public double latitude;

    public Date receiveDate;

    public String receivePDate;

    public int visitDuration;

    public ArrayList<SyncCustomerCallQuestionnaire> customerCallQuestionnaires;
}
