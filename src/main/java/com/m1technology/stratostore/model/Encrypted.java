package com.m1technology.stratostore.model;

import lombok.Value;

@Value
public class Encrypted {
    private byte[] ciphertext;
    private byte[] key;
}