package com.maryam.masar.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SaudiNationalIdValidator implements ConstraintValidator<ValidSaudiNationalId, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // let @NotBlank handle nullness separately
        }
        // Saudi National ID / Iqama: exactly 10 digits, starting with 1 (citizen) or 2 (resident)
        return value.matches("^[12]\\d{9}$");
    }
}