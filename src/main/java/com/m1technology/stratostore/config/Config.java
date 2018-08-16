package com.m1technology.stratostore.config;

import com.m1technology.stratostore.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public StoreService storeService() {
        return new StoreServiceImpl();
    }

    @Bean
    public EndecService endecService() {
        return new EndecServiceOTPImpl();
    }

    @Bean
    public KeyService keyService() {
        return new KeyServiceSecureRandomImpl();
    }
}
