package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetTrackResponse {

    private String sessionId;
    private String targetName;
    private Integer frameIndex;
    private Boolean detected;
    private Boolean locked;
    private String guidanceText;
    private String guidanceLevel;
    private String horizontalDirection;
    private String verticalDirection;
    private String estimatedDistance;
    private String proximityLevel;
    private Integer confidence;
    private String suggestedHaptic;
    private String debugSummary;
    private Integer relativeAngle;
    private TargetBox targetBox;
    private String motionHint;
    private LocalDateTime analyzedAt;
}
