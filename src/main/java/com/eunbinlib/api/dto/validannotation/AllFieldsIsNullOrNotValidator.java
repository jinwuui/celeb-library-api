package com.eunbinlib.api.dto.validannotation;

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
            Object fieldValue = getFieldValue(o, field);

            if (fieldValue != null) {
                return true;
            }
        }
        return false;
    }

    private Object getFieldValue(Object object, String fieldName) {
        Class<?> clazz = object.getClass();

        try {
            Field dateField = clazz.getDeclaredField(fieldName);
            dateField.setAccessible(true);
            Object target = dateField.get(object);

            return target;
        } catch (NoSuchFieldException e) {
            log.error("NoSuchFieldException", e);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException", e);
        } catch (Exception e) {
            log.error("Exception", e);
        }

        return null;
    }
}

