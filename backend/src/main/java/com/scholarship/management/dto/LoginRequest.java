package com.scholarship.management.dto;

import jakarta.validation.constraints.NotBlank;

/** Data sent by the Login page. */
public record LoginRequest(
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password) {
}
