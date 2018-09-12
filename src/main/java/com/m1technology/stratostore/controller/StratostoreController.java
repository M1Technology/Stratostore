package com.m1technology.stratostore.controller;

import com.m1technology.stratostore.model.Share;
import com.m1technology.stratostore.service.StratoStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class StratostoreController {

    @Autowired
    private StratoStoreService stratoStoreService;

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> upsert(@PathVariable("id") String id, @RequestParam("content") MultipartFile content) throws IOException {
        stratoStoreService.upsert(id, new Share(MediaType.parseMediaType(content.getContentType()), content.getSize(), content.getInputStream()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Resource> read(@PathVariable("id") String id) {
        Share share = stratoStoreService.read(id);
        InputStreamResource inputStreamResource = new InputStreamResource(share.getContent());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(share.getContentLength());
        headers.setContentType(share.getMediaType());
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        stratoStoreService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}