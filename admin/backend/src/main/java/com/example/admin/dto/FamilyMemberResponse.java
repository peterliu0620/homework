package com.example.admin.dto;

import java.time.LocalDateTime;

public record FamilyMemberResponse(
        Long id,
        String name,
        String phone,
        String email,
        long bindingCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
