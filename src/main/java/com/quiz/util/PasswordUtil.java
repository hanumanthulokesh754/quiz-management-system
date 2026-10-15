package com.quiz.util;

import org.mindrot.jbcrypt.BCrypt;

/** Password hashing with BCrypt (salted, slow by design). */
public final class PasswordUtil {

    // Used to keep login timing similar when the e-mail does not exist.
    private static final String DUMMY_HASH = BCrypt.hashpw("dummy-password", BCrypt.gensalt(10));

    private PasswordUtil() { }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /** @param storedHash may be null when the user does not exist */
    public static boolean verify(String plainPassword, String storedHash) {
        try {
            if (storedHash == null) {
                BCrypt.checkpw(plainPassword, DUMMY_HASH);
                return false;
            }
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            return false; // stored value is not a valid BCrypt hash
        }
    }
}
