package com.ucc.oegs.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Small helper for hashing and verifying passwords.
 *
 * <p>Passwords are never stored in plain text. A per-password random salt is
 * combined with the secret and run through SHA-256; the salt is stored
 * alongside the digest so it can be recomputed at login time. This is a
 * teaching-grade scheme (a production system would use a slow KDF such as
 * bcrypt/PBKDF2), but it demonstrates salting and one-way hashing without any
 * external dependency.</p>
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /** Produces a {@code salt:digest} string safe to persist. */
    public static String hash(String plainPassword) {
        byte[] salt = new byte[16];
        // Deterministic-free randomness sourced from the JVM's secure RNG.
        new java.security.SecureRandom().nextBytes(salt);
        String saltHex = toHex(salt);
        return saltHex + ":" + digest(saltHex, plainPassword);
    }

    /** Verifies a candidate password against a stored {@code salt:digest} value. */
    public static boolean matches(String plainPassword, String stored) {
        if (stored == null || !stored.contains(":")) {
            return false;
        }
        String[] parts = stored.split(":", 2);
        String saltHex = parts[0];
        String expected = parts[1];
        return constantTimeEquals(expected, digest(saltHex, plainPassword));
    }

    private static String digest(String saltHex, String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltHex.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            // SHA-256 is guaranteed to be present on every JVM.
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
