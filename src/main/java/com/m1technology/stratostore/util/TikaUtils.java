package com.m1technology.stratostore.util;

import com.m1technology.stratostore.exception.StratostoreException;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

public class TikaUtils {

    public static String detectType(File file) {
        Tika tika = new Tika();
        try {
            return tika.detect(file);
        } catch (IOException e) {
            throw new StratostoreException(e);
        }
    }

    public static String detectType(byte[] b) {
        Tika tika = new Tika();
        return tika.detect(b);
    }
}
