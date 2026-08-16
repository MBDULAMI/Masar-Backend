package com.maryam.masar.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TripTimesValidator.class)
public @interface ValidTripTimes {
    String message() default "Arrival time must be after departure time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}