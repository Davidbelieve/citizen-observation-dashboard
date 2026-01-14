package com.waterQualityMonitoring.crowdsourced.model.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that a postcode string matches an acceptable UK postcode format.
 * The validator is case insensitive and allows optional spaces between outward
 * and inward codes.
 */
public class ValidUkPostcodeValidator implements ConstraintValidator<ValidUkPostcode, String> {

    private static final Pattern UK_POSTCODE_PATTERN = Pattern.compile(
            "^(GIR 0AA|(?:(?:[A-PR-UWYZ][0-9][0-9A-HJKSTUW]?|"
                    + "[A-PR-UWYZ][A-HK-Y][0-9][0-9ABEHMNPRVWXY]?|"
                    + "BFPO)[ ]?[0-9][ABD-HJLNP-UW-Z]{2}))$",
            Pattern.CASE_INSENSITIVE);

    /**
     * Evaluates the postcode pattern, allowing {@code null} or blank values to
     * pass so that {@link jakarta.validation.constraints.NotBlank} can handle
     * required checks separately.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // handled by @NotBlank if required
        }
        return UK_POSTCODE_PATTERN.matcher(value.trim()).matches();
    }
}

