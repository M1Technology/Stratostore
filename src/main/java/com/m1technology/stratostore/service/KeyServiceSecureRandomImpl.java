package com.m1technology.stratostore.service;

import lombok.SneakyThrows;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class KeyServiceSecureRandomImpl implements KeyService {

    @Override
    @SneakyThrows
    public byte[] getKey(Integer length) {
        SecureRandom secureRandom;
        if (SystemUtils.IS_OS_WINDOWS) {
            secureRandom = SecureRandom.getInstance("Windows-PRNG");
        }
        else {
            secureRandom = new SecureRandom();
        }
        byte[] key = new byte[length];
        secureRandom.nextBytes(key);
        return key;
    }
}
