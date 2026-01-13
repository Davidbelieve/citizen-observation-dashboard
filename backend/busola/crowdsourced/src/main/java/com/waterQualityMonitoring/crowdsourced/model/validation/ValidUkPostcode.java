package com.waterQualityMonitoring.crowdsourced.model.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Bean validation constraint ensuring a field contains a UK-formatted postcode.
 */
@Documented
@Constraint(validatedBy = ValidUkPostcodeValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface ValidUkPostcode {

    String message() default "Invalid UK postcode format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

