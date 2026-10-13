package com.scholarship.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data sent by the Register page, the admin's student form and the student's profile form.
 * The password is required when creating an account and optional when editing one.
 */
public record UserRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") String email,
        String password,
        @NotBlank(message = "Gender is required") String gender,
        @NotBlank(message = "Category is required") String category,
        String phone,
        String course) {
}
