package com.eunbinlib.api.application.dto.validannotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllFieldsIsNullOrNotValidator.class)
public @interface AllFieldsIsNullOrNot {

    String message() default "모든 요청 정보가 비었습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields();

}
