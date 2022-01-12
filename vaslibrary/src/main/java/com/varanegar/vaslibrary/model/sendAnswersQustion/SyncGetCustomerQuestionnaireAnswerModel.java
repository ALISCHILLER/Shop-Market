package com.varanegar.vaslibrary.model.sendAnswersQustion;

import java.util.UUID;

public class SyncGetCustomerQuestionnaireAnswerModel {


    public UUID uniqueId;

    public UUID customerCallQuestionnaireUniqueId;

    public UUID questionnaireLineUniqueId;

    public String answer;

    public String options;

    public boolean hasAttachments;
}
