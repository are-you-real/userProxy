package com.example.usersProxy.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String login;
    private String name;
    private String type;
    private Integer followers;

    @JsonProperty("public_repos")
    private Integer publicRepos;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
