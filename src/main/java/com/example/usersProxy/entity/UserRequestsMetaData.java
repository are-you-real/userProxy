package com.example.usersProxy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_requests_meta_data")
public class UserRequestsMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", unique=true)
    private String login;

    @Column(name = "request_count")
    private Long requestCount;
}
