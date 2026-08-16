package com.maryam.masar.validation;

import com.maryam.masar.dto.TripPublishRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TripTimesValidator implements ConstraintValidator<ValidTripTimes, TripPublishRequest> {

    @Override
    public boolean isValid(TripPublishRequest request, ConstraintValidatorContext context) {
        if (request.getDepartureTime() == null || request.getArrivalTime() == null) {
            return true; // let @NotNull on each field handle nullness separately
        }
        return request.getArrivalTime().isAfter(request.getDepartureTime());
    }
}