package com.epam.songservice.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DurationFormatValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationFormat {
    String message() default "Invalid duration format. Expected format is mm:ss with seconds less than 60.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}