package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyProfileResponse {

    private BasicInfo basicInfo = new BasicInfo();
    private EmergencyContact emergencyContact = new EmergencyContact();
    private HealthInfo healthInfo = new HealthInfo();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfo {
        private String name;
        private String gender;
        private String age;
        private String visionLevel;
        private String address;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContact {
        private String name;
        private String relation;
        private String phone;
        private String backupPhone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthInfo {
        private String medicine;
        private String diseaseNote;
        private String allergy;
        private String reminder;
    }
}
