package com.m1technology.stratostore.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class StratostoreApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//        ConfigurableEnvironment env =  configurableApplicationContext.getEnvironment();
//        configurableApplicationContext.addBeanFactoryPostProcessor(new ReposBeanFactoryPostProcessor(env));
        configurableApplicationContext.addBeanFactoryPostProcessor(new ReposBeanFactoryPostProcessor());
    }
}
