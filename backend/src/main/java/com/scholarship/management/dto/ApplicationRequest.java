package com.scholarship.management.dto;

import jakarta.validation.constraints.*;

/** Data sent when a student applies for a scholarship or edits a pending application. */
public record ApplicationRequest(
        @NotNull(message = "Student is required") Long studentId,
        @NotNull(message = "Scholarship is required") Long scholarshipId,
        @NotBlank(message = "Course is required") String course,
        @NotNull(message = "Percentage is required")
        @DecimalMin(value = "0", message = "Percentage must be between 0 and 100")
        @DecimalMax(value = "100", message = "Percentage must be between 0 and 100") Double percentage,
        @NotNull(message = "Annual family income is required")
        @PositiveOrZero(message = "Income cannot be negative") Long annualIncome) {
}
