package com.eunbinlib.api.application.utils;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptUtils {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean isNotMatch(String plainPassword, String hashedPassword) {
        return !BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
