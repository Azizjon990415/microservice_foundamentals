package com.epam.songservice.exception;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DurationFormatValidator implements ConstraintValidator<DurationFormat, String> {

    @Override
    public void initialize(DurationFormat constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // @NotBlank and @NotNull will handle this case
        }
        String[] parts = value.split(":");
        if (parts.length != 2) {
            return false;
        }
        try {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return seconds < 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}