package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encoded;

public interface EndecService {
    Encoded encode(byte[] data);
    byte[] decode (Encoded encoded);
}
