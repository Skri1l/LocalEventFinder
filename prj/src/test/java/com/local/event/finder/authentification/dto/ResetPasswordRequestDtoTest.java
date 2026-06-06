package com.local.event.finder.authentification.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.local.event.finder.authentication.dto.ResetPasswordRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResetPasswordRequestDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenPasswordIsValid() {
        ResetPasswordRequestDto dto =
                new ResetPasswordRequestDto(
                        "some-reset-token",
                        "Password123"
                );

        Set<ConstraintViolation<ResetPasswordRequestDto>> violations =
                validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidation_whenPasswordIsBlank() {
        ResetPasswordRequestDto dto =
                new ResetPasswordRequestDto(
                        "some-reset-token",
                        ""
                );

        Set<ConstraintViolation<ResetPasswordRequestDto>> violations =
                validator.validate(dto);

        assertEquals(2, violations.size());
    }

    @Test
    void shouldFailValidation_whenPasswordDoesNotMatchPattern() {
        ResetPasswordRequestDto dto =
                new ResetPasswordRequestDto(
                        "some-reset-token",
                        "password"
                );

        Set<ConstraintViolation<ResetPasswordRequestDto>> violations =
                validator.validate(dto);

        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailValidation_whenPasswordIsNull() {
        ResetPasswordRequestDto dto =
                new ResetPasswordRequestDto(
                        "some-reset-token",
                        null
                );

        Set<ConstraintViolation<ResetPasswordRequestDto>> violations =
                validator.validate(dto);

        assertEquals(1, violations.size());
    }
}
