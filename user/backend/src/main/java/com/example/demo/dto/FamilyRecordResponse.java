package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FamilyRecordResponse {

    private Long id;
    private String time;
    private String summary;
    private String scene;
    private String imageLabel;
    private String location;
    private String note;
    private Boolean riskAlert;
    private String riskText;
}
