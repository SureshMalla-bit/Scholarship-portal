package com.scholarship.management.security;

import com.scholarship.management.exception.ApiException;
import com.scholarship.management.model.Role;
import org.springframework.http.HttpStatus;

/** The logged-in user, read from the token. Controllers receive it as @RequestAttribute(CurrentUser.ATTRIBUTE). */
public record CurrentUser(Long id, Role role) {

    public static final String ATTRIBUTE = "currentUser";

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    /** Stops the request unless the user is the admin. */
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the admin can do this");
        }
    }

    /** Stops the request unless the user is the admin or the student with this id. */
    public void requireSelfOrAdmin(Long studentId) {
        if (!isAdmin() && !id.equals(studentId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only see or change your own details");
        }
    }

    /** Stops the request unless the user is this student (admins do not apply for scholarships). */
    public void requireSelf(Long studentId) {
        if (isAdmin() || !id.equals(studentId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only manage your own applications");
        }
    }
}
