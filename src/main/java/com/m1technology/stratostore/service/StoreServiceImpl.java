package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Share;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private List<ContentStore<Share, String>> repos;
    @Autowired
    private SecretSharingService secretSharingService;

    @SneakyThrows
    private byte[] toByteArrayQuietly(InputStream is) {
        return IOUtils.toByteArray(is);
    }

    private Share shareFromId(String id) {
        Share s = new Share();
        s.setContentId(id);
        return s;
    }

    @Override
    public byte[] get(String key) {
        List<byte[]> shares = repos.stream().map(e -> toByteArrayQuietly(e.getContent(shareFromId(key)))).collect(Collectors.toList());
        return secretSharingService.reconstruct(shares);
    }

    @Override
    public String put(byte[] data) {
        String id = UUID.randomUUID().toString();
        List<byte[]> shares = secretSharingService.share(data, repos.size(), repos.size());
        shares.forEach(e -> repos.get(shares.indexOf(e)).setContent(shareFromId(id), new ByteArrayInputStream(data)));
        return id;
    }

    @Override
    public void delete(String key) {
        repos.forEach(e -> e.unsetContent(shareFromId(key)));
    }
}
