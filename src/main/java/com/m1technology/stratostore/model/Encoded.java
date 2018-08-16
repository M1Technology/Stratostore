package com.m1technology.stratostore.model;

import lombok.Value;

@Value
public class Encoded {
    private byte[] data;
    private byte[] key;
}
