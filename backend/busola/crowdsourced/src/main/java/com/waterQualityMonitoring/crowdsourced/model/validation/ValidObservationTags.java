package com.waterQualityMonitoring.crowdsourced.model.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validates that a collection of observation tags only contains entries from the
 * allowed observation catalogue.
 */
@Documented
@Constraint(validatedBy = ValidObservationTagsValidator.class)
@Target({ FIELD, METHOD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ValidObservationTags {
    String message() default "Invalid observation tags.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

