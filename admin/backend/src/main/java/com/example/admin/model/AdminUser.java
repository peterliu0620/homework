package com.example.admin.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUser {

    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}
