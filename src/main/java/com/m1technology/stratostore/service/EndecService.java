package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encoded;

public interface EndecService {
    Encoded encode(byte[] data);
    
    
    byte[] decode (Encoded encoded);
	
    byte[][] encode2(byte[] data, int numFragments);
    byte[] decode2(byte[][] fragments);
}
