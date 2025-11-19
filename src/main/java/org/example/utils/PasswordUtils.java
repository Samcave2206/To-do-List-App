package org.example.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean checkPassword(String raw, String hashed) {
        BCrypt.Result result = BCrypt.verifyer().verify(raw.toCharArray(), hashed);
        return result.verified;
    }
}
