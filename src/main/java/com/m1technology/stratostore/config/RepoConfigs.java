package com.m1technology.stratostore.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class RepoConfigs {
    @JsonProperty
    private List<RepoConfig> repoConfigs;

    @AllArgsConstructor
    @Data
    public static class RepoConfig {
        @JsonProperty
        private String name;
        @JsonProperty
        private RepoType type;
    }
}