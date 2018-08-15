package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encoded;
import org.springframework.beans.factory.annotation.Autowired;

public class EndecServiceOTPImpl implements EndecService{

    @Autowired
    private RandomService randomService;

    @Override
    public Encoded encode(byte[] data) {
        return null;
    }

    @Override
    public byte[] decode(Encoded encoded) {
        return new byte[0];
    }
}
