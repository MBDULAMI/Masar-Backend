package com.maryam.masar.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SaudiNationalIdValidator.class)
public @interface ValidSaudiNationalId {
    String message() default "National ID must be 10 digits starting with 1 or 2";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}