package com.m1technology.stratostore.service;

public interface ContentRepo<T, K> {
    void upsert(K id, T content);
    T read(K id);
    void delete(K id);
}
