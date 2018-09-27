package com.m1technology.stratostore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.m1technology.stratostore.exception.StratostoreException;
import com.m1technology.stratostore.service.provider.FileSystemRepo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class ReposBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final RepoConfigs repoConfigs;

    public ReposBeanFactoryPostProcessor() {
        ClassPathResource resource = new ClassPathResource("repos.yml");
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            repoConfigs = mapper.readValue(resource.getInputStream(), RepoConfigs.class);
        } catch (IOException e) {
            throw new StratostoreException(e);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        repoConfigs.getRepoConfigs()
                   .forEach(repoConfig -> beanDefinitionRegistry.registerBeanDefinition(repoConfig.getName(), BeanDefinitionBuilder
                           .genericBeanDefinition(FileSystemRepo.class)
                           .addConstructorArgValue(repoConfig.getName())
                           .setLazyInit(true)
                           .getBeanDefinition()));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
}
