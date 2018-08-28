package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Encrypted;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class SecretSharingServiceTrivialImpl implements SecretSharingService {

    @Autowired
    private EndecService endecService;

    @Override
    public List<byte[]> share(byte[] secret, int threshold, int numShares) {
        Validate.isTrue(numShares > 0, "Secret must be shared by at least one.");
        Validate.isTrue(threshold == numShares, "Trivial secret sharing scheme requires all shares for reconstruction (threshold and shares must be the same)");
        List<byte[]> shares = new ArrayList<>(numShares);
        shares.add(secret);
        for (int i=1; i<numShares; i++) {
            byte[] share = shares.remove(shares.size()-1);
            Encrypted e = endecService.encrypt(share);
            shares.add(e.getCiphertext());
            shares.add(e.getKey());
        }
        return shares;
    }

    @Override
    public byte[] reconstruct(List<byte[]> shares) {
        List<byte[]> copy = new ArrayList<>(shares);
        Iterator<byte[]> it = copy.iterator();
        byte[] x = it.next();
        it.remove();
        while (it.hasNext()) {
            x = endecService.decrypt(new Encrypted(x, it.next()));
            it.remove();
        }
        return x;
    }
}
