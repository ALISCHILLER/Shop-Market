package com.varanegar.vaslibrary.manager.questionnaire;

public class EPollResultViewModel {
    public EPollResultViewModel(int formId, int answerId){
        this.FormId = formId;
        this.AnswerId = answerId;
    }
    public int FormId;
    public int AnswerId;
}
