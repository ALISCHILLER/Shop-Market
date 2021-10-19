package com.varanegar.framework.validation;

import java.util.List;

/**
 * Created by A.Torabi on 6/2/2018.
 */

public class ValidationException extends Exception {
    public List<ConstraintViolation> getViolations() {
        return violations;
    }

    private final List<ConstraintViolation> violations;

    public ValidationException(List<ConstraintViolation> violations) {
        this.violations = violations;
    }

    @Override
    public String getMessage() {
        return "Validation failed: " + Validator.toString(violations);
    }
}
