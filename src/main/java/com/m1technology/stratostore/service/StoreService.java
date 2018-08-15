package com.m1technology.stratostore.service;

public interface StoreService {
    byte[] get(String key);
    String put(byte[] data);
    void delete(String key);
}
