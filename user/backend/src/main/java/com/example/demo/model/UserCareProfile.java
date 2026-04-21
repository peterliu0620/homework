package com.example.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCareProfile {

    private Long id;
    private Long userId;
    private String subjectName;
    private String gender;
    private String age;
    private String visionLevel;
    private String address;
    private String notes;
    private String emergencyName;
    private String emergencyRelation;
    private String emergencyPhone;
    private String emergencyBackupPhone;
    private String medicine;
    private String diseaseNote;
    private String allergy;
    private String reminder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
