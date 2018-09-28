package com.m1technology.stratostore.config;

public enum RepoType {
    FILE_SYSTEM("FileSystemRepo"),
    S3("S3Repo");

    private String name;

    RepoType(String name) {
        this.name = name;
    }

    public String toFullyQualifiedClassName() {
        return "com.m1technology.stratostore.service.provider." + name;
    }
}
