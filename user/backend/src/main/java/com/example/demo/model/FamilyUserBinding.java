package com.example.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilyUserBinding {

    private Long id;
    private Long familyUserId;
    private Long visionUserId;
    private String relationship;
    private LocalDateTime createdAt;
}
