package com.varanegar.vaslibrary.webapi.tour;

import java.util.UUID;

/**
 * Created by A.Torabi on 11/2/2017.
 */

public class SyncGetCustomerCallQuestionnaireAnswerViewModel {
    // UniqueId = Token of the pictures.
    // Each question can contain several picture attachments.
    // All picture share a Token id. Each should have a different id for file name
    public UUID UniqueId;
    public UUID CustomerCallQuestionnaireUniqueId;
    public UUID QuestionnaireLineUniqueId;
    public String Answer;
    public boolean HasAttachments;
    public String options;
}
