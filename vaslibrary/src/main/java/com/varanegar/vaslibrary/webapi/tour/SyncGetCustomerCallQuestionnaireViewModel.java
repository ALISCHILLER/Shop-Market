package com.varanegar.vaslibrary.webapi.tour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/2/2017.
 */

public class SyncGetCustomerCallQuestionnaireViewModel {
    //public UUID UniqueId ;
    //public UUID CustomerCallUniqueId ;
    public UUID QuestionnaireUniqueId ;
    public List<SyncGetCustomerCallQuestionnaireAnswerViewModel> Answers = new ArrayList<>();
}
