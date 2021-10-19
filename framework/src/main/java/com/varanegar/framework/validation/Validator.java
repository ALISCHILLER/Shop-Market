package com.varanegar.framework.validation;

import android.os.Handler;
import android.util.Pair;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.Linq;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by atp on 12/24/2016.
 */
public class Validator<T extends BaseModel> {
    public void validate(final ValidationListener validationListener) {
        final ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Field[] allFields = validationListener.getClass().getFields();
                for (Field field :
                        allFields) {
                    try {
                        Object fieldObject = field.get(validationListener);
                        Annotation[] annotations = field.getAnnotations();
                        for (Annotation annotation :
                                annotations) {
                            Validation validationAnnotation = annotation.annotationType().getAnnotation(Validation.class);
                            if (validationAnnotation != null) {
                                try {
                                    ValidationChecker validationChecker = validationAnnotation.checkerClass().newInstance();
                                    validationChecker.createRuleFromAnnotation(annotation);
                                    boolean result = validationChecker.validate(fieldObject);
                                    if (!result) {
                                        ConstraintViolation violation = new ConstraintViolation(validationChecker, field, "");
                                        ValidationError validationError = new ValidationError(violation, fieldObject);
                                        errors.add(validationError);
                                    }
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (errors.size() > 0) {
                            validationListener.onValidationFailed(errors);
                        } else {
                            validationListener.onValidationSucceeded();
                        }
                    }
                });
            }
        });

        thread.start();

    }

    public void validate(final T model) throws ValidationException {
        final List<ConstraintViolation> violations = new ArrayList<>();
        check(model, violations);
        if (violations.size() > 0) {
            throw new ValidationException(violations);
        }
    }

    public void validate(final Iterable<T> models) throws ValidationException {
        final List<ConstraintViolation> violations = new ArrayList<>();
        check(models, violations);
        if (violations.size() > 0) {
            throw new ValidationException(violations);
        }
    }

    private void check(T model, List<ConstraintViolation> violations) {
        Field[] allFields = model.getClass().getFields();
        for (Field field :
                allFields) {
            try {
                Object fieldObject = field.get(model);
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation :
                        annotations) {
                    Validation validationAnnotation = annotation.annotationType().getAnnotation(Validation.class);
                    if (validationAnnotation != null) {
                        try {
                            ValidationChecker validationChecker = validationAnnotation.checkerClass().newInstance();
                            validationChecker.createRuleFromAnnotation(annotation);
                            boolean result = validationChecker.validate(fieldObject);
                            if (!result) {
                                ConstraintViolation violation = new ConstraintViolation(validationChecker, field, "");
                                violations.add(violation);
                            }
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void check(final Iterable<T> models, List<ConstraintViolation> violations) {
        HashMap<Field, List<Pair<ValidationChecker, Pair<Annotation, Validation>>>> validationCheckersMap = new HashMap<>();
        try {
            T first = models.iterator().next();
            Field[] allFields = first.getClass().getFields();
            for (Field field :
                    allFields) {
                try {
                    Annotation[] annotations = field.getAnnotations();
                    for (Annotation annotation :
                            annotations) {
                        Validation validationAnnotation = annotation.annotationType().getAnnotation(Validation.class);
                        if (validationAnnotation != null) {
                            try {
                                ValidationChecker validationChecker = validationAnnotation.checkerClass().newInstance();
                                if (validationCheckersMap.containsKey(field)) {
                                    validationCheckersMap.get(field).add(new Pair<>(validationChecker, new Pair<>(annotation, validationAnnotation)));
                                } else {
                                    List<Pair<ValidationChecker, Pair<Annotation, Validation>>> checkers = new ArrayList<>();
                                    checkers.add(new Pair<>(validationChecker, new Pair<>(annotation, validationAnnotation)));
                                    validationCheckersMap.put(field, checkers);
                                }
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            Set<Field> annotatedFields = validationCheckersMap.keySet();
            for (T model :
                    models) {
                for (Field field :
                        annotatedFields) {
                    try {
                        Object fieldObject = field.get(model);
                        List<Pair<ValidationChecker, Pair<Annotation, Validation>>> validationCheckers = validationCheckersMap.get(field);
                        if (validationCheckers != null) {
                            for (Pair<ValidationChecker, Pair<Annotation, Validation>> pair :
                                    validationCheckers) {
                                pair.first.createRuleFromAnnotation(pair.second.first);
                                boolean result = pair.first.validate(fieldObject);
                                if (!result) {
                                    violations.add(new ConstraintViolation(pair.first, field, ""));
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NoSuchElementException ex) {
            return;
        }

    }

    public static String toString(List<ConstraintViolation> errors) {
        return Linq.mapMerge(errors, new Linq.Map<ConstraintViolation, String>() {
            @Override
            public String run(ConstraintViolation item) {
                return item.getLog();
            }
        }, new Linq.Merge<String>() {
            @Override
            public String run(String item1, String item2) {
                return item1.concat(" \n ").concat(item2);
            }
        });
    }
}
