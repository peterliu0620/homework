package com.example.demo.dto;

import lombok.Data;

@Data
public class FamilyProfileRequest {

    private BasicInfo basicInfo = new BasicInfo();
    private EmergencyContact emergencyContact = new EmergencyContact();
    private HealthInfo healthInfo = new HealthInfo();

    @Data
    public static class BasicInfo {
        private String name;
        private String gender;
        private String age;
        private String visionLevel;
        private String address;
        private String notes;
    }

    @Data
    public static class EmergencyContact {
        private String name;
        private String relation;
        private String phone;
        private String backupPhone;
    }

    @Data
    public static class HealthInfo {
        private String medicine;
        private String diseaseNote;
        private String allergy;
        private String reminder;
    }
}
