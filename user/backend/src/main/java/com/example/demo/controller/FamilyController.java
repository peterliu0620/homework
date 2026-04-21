package com.example.demo.controller;

import com.example.demo.dto.FamilyDashboardResponse;
import com.example.demo.dto.FamilyProfileRequest;
import com.example.demo.dto.FamilyProfileResponse;
import com.example.demo.dto.FamilyRecordResponse;
import com.example.demo.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @GetMapping("/dashboard")
    public FamilyDashboardResponse dashboard(@RequestParam Long familyUserId) {
        return familyService.getDashboard(familyUserId);
    }

    @GetMapping("/records")
    public List<FamilyRecordResponse> records(@RequestParam Long familyUserId,
                                              @RequestParam(defaultValue = "10") Integer limit) {
        return familyService.listRecords(familyUserId, limit);
    }

    @GetMapping("/profile")
    public FamilyProfileResponse profile(@RequestParam Long familyUserId) {
        return familyService.getProfile(familyUserId);
    }

    @PostMapping("/profile")
    public FamilyProfileResponse saveProfile(@RequestParam Long familyUserId,
                                             @RequestBody FamilyProfileRequest request) {
        return familyService.saveProfile(familyUserId, request);
    }
}
