package com.varanegar.framework.validation;

import android.app.Activity;
import android.widget.Toast;

import com.varanegar.framework.R;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atp on 12/24/2016.
 */
public class FormValidator {
    private final Activity activity;
    private List<ValidatorField> fieldList;

    public FormValidator(Activity activity) {
        this.activity = activity;
    }

    public void validate() throws ValidationException {
        final List<ConstraintViolation> violations = new ArrayList<>();
        for (ValidatorField field :
                fieldList) {
            Object fieldObject = field.fieldObject;
            for (ValidationChecker validationChecker :
                    field.rules) {
                boolean result = validationChecker.validate(fieldObject);
                if (!result) {
                    ConstraintViolation violation = new ConstraintViolation(validationChecker, fieldObject, field.title);
                    violations.add(violation);
                }
            }
        }
        if (violations.size() > 0) {
            throw new ValidationException(violations);
        }
    }

    public void validate(ValidationListener validationListener) {
        try {
            clearErrors();
            validate();
            validationListener.onValidationSucceeded();
        } catch (ValidationException e) {
            List<ValidationError> errors = new ArrayList<>();
            for (ConstraintViolation v :
                    e.getViolations()) {
                ValidationError validationError = new ValidationError(v, v.getFieldObject());
                errors.add(validationError);
            }
            validationListener.onValidationFailed(errors);
        }
    }


    public void addField(Object fieldObject, String title, ValidationChecker... validationRules) {
        if (fieldList == null)
            fieldList = new ArrayList<>();
        fieldList.add(new ValidatorField(fieldObject, title, validationRules));
    }

    public void clearErrors() {
        if (activity != null && !activity.isFinishing()) {
            for (ValidatorField field :
                    fieldList) {
                if (field.fieldObject instanceof PairedItemsEditable)
                    ((PairedItemsEditable) field.fieldObject).setError(null);
                else if (field.fieldObject instanceof PairedItemsSpinner)
                    ((PairedItemsSpinner) field.fieldObject).setError(null);
            }
        }
    }

    public void showErrors(List<ValidationError> errors) {
        if (activity != null && !activity.isFinishing()) {
            for (ValidationError error :
                    errors) {
                String errorCode = error.getViolation().getSimpleName();
                String errorMessage = activity.getString(R.string.error);
                switch (errorCode) {
                    case "NotEmptyChecker":
                        errorMessage = activity.getString(R.string.not_empty);
                        break;
                    case "PhoneNumberChecker":
                        errorMessage = activity.getString(R.string.phone_number_is_not_valid);
                        break;
                    case "LengthChecker":
                        errorMessage = activity.getString(R.string.length_is_not_valid);
                        break;
                    case "NotNullChecker":
                        errorMessage = activity.getString(R.string.not_empty);
                        break;
                    case "IraniNationalCodeChecker":
                        errorMessage = activity.getString(R.string.national_code_is_not_valid);
                        break;
                    case "CompareValueChecker":
                        errorMessage = activity.getString(R.string.value_is_not_valid);
                        break;
                }
                if (error.getField() instanceof PairedItemsEditable)
                    ((PairedItemsEditable) error.getField()).setError(errorMessage);
                else if (error.getField() instanceof PairedItemsSpinner)
                    ((PairedItemsSpinner) error.getField()).setError(errorMessage);
                else
                    Toast.makeText(activity, error.getFieldTitle() + ": " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
