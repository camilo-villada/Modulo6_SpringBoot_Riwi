package com.java.eventify.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NoPastEventsValidator implements ConstraintValidator<NoPastEvents, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; 
        }
        return !value.isBefore(LocalDate.now());
    }
}
