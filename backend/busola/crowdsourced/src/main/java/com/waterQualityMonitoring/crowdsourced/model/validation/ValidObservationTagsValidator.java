package com.waterQualityMonitoring.crowdsourced.model.validation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.waterQualityMonitoring.crowdsourced.model.ObservationTagType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Ensures that submitted observation tags are part of the supported catalogue
 * (Clear, Cloudy, Murky, Foamy, Oily, Discoloured, Presence of Odour).
 */
public class ValidObservationTagsValidator implements ConstraintValidator<ValidObservationTags, List<String>> {

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<String> invalidTags = value.stream()
                .map(tag -> tag == null ? null : tag.trim().replaceAll("\\s+", " "))
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        invalidTags.removeIf(tag -> tag != null && ObservationTagType.fromValue(tag).isPresent());

        if (invalidTags.contains(null) || invalidTags.contains("")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Observation tags cannot be blank.")
                    .addConstraintViolation();
            return false;
        }

        if (!invalidTags.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Invalid observation tags: " + invalidTags + ". Allowed values: " + ObservationTagType.labels())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

