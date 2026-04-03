package com.example.admin.controller;

import com.example.admin.dto.AppUserCreateRequest;
import com.example.admin.dto.AppUserResponse;
import com.example.admin.dto.AppUserUpdateRequest;
import com.example.admin.service.AppUserAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/app-users")
@RequiredArgsConstructor
public class AppUserAdminController {

    private final AppUserAdminService appUserAdminService;

    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String keyword) {
        List<AppUserResponse> users = appUserAdminService.list(keyword);
        return Map.of("success", true, "data", users);
    }

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody AppUserCreateRequest request) {
        return Map.of("success", true, "data", appUserAdminService.create(request));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody AppUserUpdateRequest request) {
        return Map.of("success", true, "data", appUserAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        appUserAdminService.delete(id);
        return Map.of("success", true, "data", true);
    }
}
