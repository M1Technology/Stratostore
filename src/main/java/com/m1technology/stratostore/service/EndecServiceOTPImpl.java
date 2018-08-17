package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encoded;

import java.util.Arrays;

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
        
        //TODO: Return Fragment[] (or simply byte[]) instead of encoded and key?
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
    
    @Override
    public byte[][] encode2(byte[] data, int numFragments) {
    	
    	final byte[][] fragments = new byte[numFragments][data.length];
    	
    	//TODO: use array copy function?
    	for (int j = 0; j < data.length; j++) {
    		fragments[0][j] = data[j];
    	}
    	
    	
        for (int i = 1; i < numFragments; i++) {
    		fragments[i] = keyService.getKey(data.length); //the last fragment is always the key.

    		for (int j = 0; j < data.length; j++) {
    			fragments[i-1][j] = (byte) (fragments[i-1][j] ^ fragments[i][j]); 

    			//TODO: Mix-up the key and the data so that there isn't one long "key" string.            	
        	} 

        }
        return fragments;
    }
    
    @Override
    public byte[] decode2(byte[][] fragments) {

        byte[] decoded = new byte[fragments[0].length];
        decoded = fragments[0];
        
        for (int i = 1; i < fragments.length; i++) {
        	for (int j = 0; j <  fragments[0].length; j++) {
        		decoded[j] = (byte) (decoded[j] ^ fragments[i][j]); //combine the fragments one at a time with fragment 0
        		
        	} 
        }
        
        return decoded;
    }
    
    
}
