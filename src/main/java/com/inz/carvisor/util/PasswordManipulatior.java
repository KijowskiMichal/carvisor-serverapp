package com.inz.carvisor.util;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordManipulatior {

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
