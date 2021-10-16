package com.inz.carvisor.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
