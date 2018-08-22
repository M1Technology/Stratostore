package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encrypted;

public interface EndecService {
    Encrypted encrypt(byte[] data);
    byte[] decrypt(Encrypted encrypted);
}
