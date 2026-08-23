package com.susanthika.TicketProject.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {


    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return false;
        }

        return email.contains("@")
                && email.contains(".")
                && email.indexOf("@") > 0
                && email.indexOf(".") > email.indexOf("@") + 1
                && email.indexOf(".") < email.length() - 1;
    }
}
