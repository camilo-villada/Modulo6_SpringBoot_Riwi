package com.java.eventify.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoPastEventsValidator.class)
@Documented
public @interface NoPastEvents {
    String message() default "The event date must be today or in the future";
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
