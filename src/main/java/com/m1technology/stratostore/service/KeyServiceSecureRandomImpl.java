package com.m1technology.stratostore.service;

import com.m1technology.stratostore.exception.StratostoreException;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class KeyServiceSecureRandomImpl implements KeyService {

    @Override
    public byte[] getKey(Integer length) {
        SecureRandom secureRandom;
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                secureRandom = SecureRandom.getInstance("Windows-PRNG");
            } catch (NoSuchAlgorithmException e) {
                throw new StratostoreException(e);
            }
        }
        else {
            secureRandom = new SecureRandom();
        }
        byte[] key = new byte[length];
        secureRandom.nextBytes(key);
        return key;
    }
}
