package com.varanegar.framework.validation.annotations.validvalue;

import android.widget.EditText;

import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.validation.ValidationChecker;
import com.varanegar.framework.validation.WrongValidatorException;

/**
 * Created by A.Jafarzadeh on 6/24/2018.
 */

public class CompareValueChecker extends ValidationChecker<Compare> {
    private Operator operator;
    private String value;

    public CompareValueChecker(){
    }

    public CompareValueChecker(Operator operator, String value){
        this.operator = operator;
        this.value = value;
    }

    @Override
    public void createRuleFromAnnotation(Compare annotation) {
        operator = annotation.operator();
        value = annotation.value();
    }

    @Override
    public boolean validate(Object fieldObject) {
        if (fieldObject == null)
            return false;

        String str = "";
        if (fieldObject instanceof EditText) {
            EditText editText = (EditText) fieldObject;
            str = editText.getText().toString();
        } else if (fieldObject instanceof String) {
            str = (String) fieldObject;
        } else if (fieldObject instanceof PairedItemsEditable) {
            PairedItemsEditable item = (PairedItemsEditable) fieldObject;
            str = item.getValue();
        } else
            throw new WrongValidatorException(fieldObject.getClass());


        if (str == null || str.isEmpty()) {
            return operator == Operator.IsNull;
        }

        if (operator == Operator.Equals && str.equals(value))
            return true;
        else if (operator == Operator.NotEquals && !str.equals(value))
            return true;
        else if (operator == Operator.LessThan && str.compareTo(value) < 0)
            return true;
        else if (operator == Operator.MoreThan && str.compareTo(value) > 0)
            return true;
        else if (operator == Operator.EqualsOrMoreThan && str.compareTo(value) >= 0)
            return true;
        else if (operator == Operator.EqualsOrLessThan && str.compareTo(value) <= 0)
            return true;
        else
            return false;
    }
}
