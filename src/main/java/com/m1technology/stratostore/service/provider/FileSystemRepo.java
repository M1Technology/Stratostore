package com.m1technology.stratostore.service.provider;

import com.m1technology.stratostore.exception.StratostoreException;
import com.m1technology.stratostore.model.Share;
import com.m1technology.stratostore.service.ContentRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;

import java.io.*;

@AllArgsConstructor
public class FileSystemRepo implements ContentRepo<Share, String> {

    private String name;
    private String basePath;

    public FileSystemRepo(String name) {
        this(name, FileUtils.getTempDirectoryPath() + File.separator + name);
    }

    @Override
    public void upsert(String id, Share share) {
        try(InputStream i = share.getContent()) {
            FileUtils.copyInputStreamToFile(i, new File(buildPath(id)));
        } catch (IOException e) {
           throw new StratostoreException(e);
        }
    }

    @Override
    public Share read(String id) {
        File file = new File(buildPath(id));
        Tika tika = new Tika();
        String type;
        try {
            type = tika.detect(file);
        } catch (IOException e) {
            throw new StratostoreException(e);
        }
        InputStream content;
        try {
            content = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new StratostoreException(e);
        }
        return new Share(MediaType.parseMediaType(type), file.length(), content);
    }

    @Override
    public void delete(String id) {
        FileUtils.deleteQuietly(new File(buildPath(id)));
    }

    private String buildPath(String id) {
        return basePath + File.separator + id;
    }
}
