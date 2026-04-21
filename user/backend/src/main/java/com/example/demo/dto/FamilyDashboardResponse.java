package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FamilyDashboardResponse {

    private Long visionUserId;
    private String visionUserNickname;
    private Integer todayCount;
    private Integer riskCount;
    private Integer recordsCount;
    private Integer completedSections;
    private String recentRiskText;
}
