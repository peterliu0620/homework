package com.example.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FamilyMemberBindingRequest {

    @NotNull(message = "userId 不能为空")
    private Long userId;

    @NotBlank(message = "关系不能为空")
    private String relationship;

    private String status;
}
