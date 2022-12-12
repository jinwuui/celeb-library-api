package com.eunbinlib.api.application.dto.validannotation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

@Slf4j
public class AllFieldsIsNullOrNotValidator implements ConstraintValidator<AllFieldsIsNullOrNot, Object> {

    private String message;

    private String[] fields;

    @Override
    public void initialize(AllFieldsIsNullOrNot constraintAnnotation) {
        message = constraintAnnotation.message();
        fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        for (String field : fields) {
            if (isExistField(o, field)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExistField(Object object, String fieldName) {

        try {
            Class<?> clazz = object.getClass();

            Field dateField = clazz.getDeclaredField(fieldName);
            dateField.setAccessible(true);
            Object target = dateField.get(object);

            if (target != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("Exception", e);
        }

        return false;
    }
}

