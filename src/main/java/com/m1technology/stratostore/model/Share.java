package com.m1technology.stratostore.model;

import lombok.Value;
import org.springframework.http.MediaType;

import java.io.InputStream;

@Value
public class Share {
    private MediaType mediaType;
    private Long contentLength;
    private InputStream content;
}
