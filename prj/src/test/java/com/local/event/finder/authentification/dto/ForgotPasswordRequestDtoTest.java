package com.local.event.finder.authentification.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.local.event.finder.authentication.dto.ForgotPasswordRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForgotPasswordRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenEmailIsValid() {
        ForgotPasswordRequestDto dto =
                new ForgotPasswordRequestDto("test@mail.com");

        Set<ConstraintViolation<ForgotPasswordRequestDto>> violations =
                validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidation_whenEmailIsInvalid() {
        ForgotPasswordRequestDto dto =
                new ForgotPasswordRequestDto("invalid-email");

        Set<ConstraintViolation<ForgotPasswordRequestDto>> violations =
                validator.validate(dto);

        assertEquals(1, violations.size());
    }

    @Test
    void shouldPassValidation_whenEmailIsNull() {
        ForgotPasswordRequestDto dto =
                new ForgotPasswordRequestDto(null);

        Set<ConstraintViolation<ForgotPasswordRequestDto>> violations =
                validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
