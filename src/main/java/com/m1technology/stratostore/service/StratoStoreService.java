package com.m1technology.stratostore.service;

import com.m1technology.stratostore.model.Share;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StratoStoreService implements ContentRepo<Share, String> {

    @Autowired
    private List<ContentRepo<Share, String>> repos;
    @Autowired
    private SecretSharingService secretSharingService;

    @SneakyThrows
    private byte[] toByteArrayQuietly(InputStream is) {
        return IOUtils.toByteArray(is);
    }

    @Override
    public Share read(String id) {
        List<Share> shares = repos.stream()
                                  .map(repo -> repo.read(id))
                                  .collect(Collectors.toList());
        List<byte[]> shareContents = shares.stream()
                                           .map(Share::getContent)
                                           .map(this::toByteArrayQuietly)
                                           .collect(Collectors.toList());
        byte[] content = secretSharingService.reconstruct(shareContents);
        return new Share(shares.get(0).getMediaType(), Long.valueOf(content.length), new ByteArrayInputStream(content));
    }

    @Override
    public void upsert(String id, Share share) {
        List<byte[]> shareContents = secretSharingService.share(toByteArrayQuietly(share.getContent()), repos.size(), repos.size());
        shareContents.forEach(s -> repos.get(shareContents.indexOf(s))
                                        .upsert(id, new Share(share.getMediaType(), share.getContentLength(), new ByteArrayInputStream(s))));
    }

    @Override
    public void delete(String id) {
        repos.forEach(repo -> repo.delete(id));
    }
}
