package com.m1technology.stratostore.service;

import java.util.List;

public interface SecretSharingService {
    List<byte[]> share(byte[] secret, int threshold, int numShares);
    byte[] reconstruct(List<byte[]> shares);
}
