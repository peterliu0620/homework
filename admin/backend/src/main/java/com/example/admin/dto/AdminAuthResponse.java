package com.example.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminAuthResponse {

    private Long id;
    private String username;
    private String nickname;
    private String token;
}
