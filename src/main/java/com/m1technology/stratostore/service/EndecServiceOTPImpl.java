package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encoded;
import org.springframework.beans.factory.annotation.Autowired;

public class EndecServiceOTPImpl implements EndecService{

    @Autowired
    private KeyService keyService;

    @Override
    public Encoded encode(byte[] data) {
        final byte[] encoded = new byte[data.length];
        final byte[] key = keyService.getKey(data.length);

        for (int i = 0; i < data.length; i++) {
            encoded[i] = (byte) (data[i] ^ key[i]);
        }
        return new Encoded(encoded, key);
    }

    @Override
    public byte[] decode(Encoded encoded) {
        final byte[] decoded = new byte[encoded.getData().length];

        for (int i = 0; i < encoded.getData().length; i++) {
            decoded[i] = (byte) (encoded.getData()[i] ^ encoded.getKey()[i]);
        }
        return decoded;
    }
}
