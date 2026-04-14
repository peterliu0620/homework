package com.example.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FamilyMemberRequest {

    @NotBlank(message = "家属姓名不能为空")
    private String name;

    @Pattern(regexp = "^$|^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(
            regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "邮箱格式不正确"
    )
    private String email;
}
