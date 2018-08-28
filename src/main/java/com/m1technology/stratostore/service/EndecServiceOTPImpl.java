package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encrypted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EndecServiceOTPImpl implements EndecService{

    @Autowired
    private KeyService keyService;

    @Override
    public Encrypted encrypt(byte[] data) {
        final byte[] encoded = new byte[data.length];
        final byte[] key = keyService.getKey(data.length);

        for (int i = 0; i < data.length; i++) {
            encoded[i] = (byte) (data[i] ^ key[i]);
        }
        return new Encrypted(encoded, key);
    }

    @Override
    public byte[] decrypt(Encrypted encrypted) {
        final byte[] decoded = new byte[encrypted.getCiphertext().length];

        for (int i = 0; i < encrypted.getCiphertext().length; i++) {
            decoded[i] = (byte) (encrypted.getCiphertext()[i] ^ encrypted.getKey()[i]);
        }
        return decoded;
    }
}