package com.scholarship.management.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarship.management.model.Role;
import com.scholarship.management.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/**
 * Creates and checks login tokens (standard JWT signed with HMAC-SHA256).
 * The token holds the user's id, role and an expiry time; the signature stops anyone from changing them.
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final String HEADER = ENCODER.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

    private final ObjectMapper json;
    private final byte[] secret;
    private final long validSeconds;

    public TokenService(ObjectMapper json,
                        @Value("${app.jwt.secret}") String secret,
                        @Value("${app.jwt.valid-hours}") long validHours) {
        this.json = json;
        this.validSeconds = validHours * 3600;
        if (secret == null || secret.isBlank()) {
            // Without JWT_SECRET a random key is used, so everyone is logged out when the server restarts
            log.warn("JWT_SECRET is not set - using a random key. Set it before deploying.");
            this.secret = new byte[32];
            new SecureRandom().nextBytes(this.secret);
        } else {
            this.secret = secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    public String create(User user) {
        Map<String, Object> claims = Map.of(
                "sub", user.getId().toString(),
                "role", user.getRole().name(),
                "exp", Instant.now().getEpochSecond() + validSeconds);
        try {
            String payload = ENCODER.encodeToString(json.writeValueAsBytes(claims));
            String unsigned = HEADER + "." + payload;
            return unsigned + "." + ENCODER.encodeToString(sign(unsigned));
        } catch (Exception e) {
            throw new IllegalStateException("Could not create token", e);
        }
    }

    /** Returns the user in the token, or empty if the token is missing, changed or expired. */
    public Optional<CurrentUser> read(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !parts[0].equals(HEADER)) return Optional.empty();
            byte[] expected = sign(parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expected, DECODER.decode(parts[2]))) return Optional.empty();
            JsonNode claims = json.readTree(DECODER.decode(parts[1]));
            if (claims.path("exp").asLong() < Instant.now().getEpochSecond()) return Optional.empty();
            return Optional.of(new CurrentUser(
                    Long.valueOf(claims.path("sub").asText()),
                    Role.valueOf(claims.path("role").asText())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private byte[] sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
}
