package com.m1technology.stratostore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private SecretSharingService secretSharingService;

    @Override
    public byte[] get(String key) {
        return new byte[0];
    }

    @Override
    public String put(byte[] data) {
        return null;
    }

    @Override
    public void delete(String key) {

    }
}
