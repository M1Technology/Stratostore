package com.m1technology.stratostore.model;

import lombok.Data;
import lombok.Value;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Date created = new Date();
    private String summary;

    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    private String mimeType = "application/octet-stream";
}
