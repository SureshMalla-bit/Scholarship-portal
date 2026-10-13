package com.scholarship.management.security;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.scholarship.management.model.User;

/** Login answer: the user's details (id, name, role ...) plus the token to send with later requests. */
public class AuthResponse {

    @JsonUnwrapped
    private final User user;
    private final String token;

    public AuthResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() { return user; }
    public String getToken() { return token; }
}
