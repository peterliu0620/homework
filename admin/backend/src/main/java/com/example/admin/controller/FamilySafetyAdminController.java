package com.example.admin.controller;

import com.example.admin.dto.FamilyBindingCreateRequest;
import com.example.admin.dto.MedicineProfileRequest;
import com.example.admin.dto.FamilyMemberBindingRequest;
import com.example.admin.dto.FamilyMemberRequest;
import com.example.admin.service.FamilySafetyAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class FamilySafetyAdminController {

    private final FamilySafetyAdminService familySafetyAdminService;

    @GetMapping("/family-members")
    public Map<String, Object> listFamilyMembers(@RequestParam(required = false) String keyword) {
        return Map.of("success", true, "data", familySafetyAdminService.listFamilyMembers(keyword));
    }

    @GetMapping("/family-members/{id}")
    public Map<String, Object> getFamilyMember(@PathVariable Long id) {
        return Map.of("success", true, "data", familySafetyAdminService.getFamilyMember(id));
    }

    @PostMapping("/family-members")
    public Map<String, Object> createFamilyMember(@Valid @RequestBody FamilyMemberRequest request) {
        return Map.of("success", true, "data", familySafetyAdminService.createFamilyMember(request));
    }

    @PutMapping("/family-members/{id}")
    public Map<String, Object> updateFamilyMember(@PathVariable Long id, @Valid @RequestBody FamilyMemberRequest request) {
        return Map.of("success", true, "data", familySafetyAdminService.updateFamilyMember(id, request));
    }

    @DeleteMapping("/family-members/{id}")
    public Map<String, Object> deleteFamilyMember(@PathVariable Long id) {
        familySafetyAdminService.deleteFamilyMember(id);
        return Map.of("success", true, "data", true);
    }

    @GetMapping("/family-members/{id}/blind-users")
    public Map<String, Object> listBlindUsers(@PathVariable Long id) {
        return Map.of("success", true, "data", familySafetyAdminService.listBindingsByFamilyMember(id));
    }

    @PostMapping("/family-members/{id}/bindings")
    public Map<String, Object> bindBlindUser(@PathVariable Long id, @Valid @RequestBody FamilyMemberBindingRequest request) {
        return Map.of("success", true, "data", familySafetyAdminService.createBinding(id, request));
    }

    @DeleteMapping("/family-members/{id}/bindings/{blindUserId}")
    public Map<String, Object> deleteBinding(@PathVariable Long id, @PathVariable Long blindUserId) {
        familySafetyAdminService.deleteBinding(id, blindUserId);
        return Map.of("success", true, "data", true);
    }

    @GetMapping("/blind-users/{id}/family-members")
    public Map<String, Object> listFamilyMembersByBlindUser(@PathVariable Long id) {
        return Map.of("success", true, "data", familySafetyAdminService.listBindingsByUser(id));
    }

    @GetMapping("/family-bindings")
    public Map<String, Object> listBindings(@RequestParam(required = false) String keyword) {
        return Map.of("success", true, "data", familySafetyAdminService.listBindings(keyword));
    }

    @PostMapping("/family-bindings")
    public Map<String, Object> createBinding(@Valid @RequestBody FamilyBindingCreateRequest request) {
        return Map.of("success", true, "data", familySafetyAdminService.createBinding(request));
    }

    @GetMapping("/shared-logs")
    public Map<String, Object> listSharedLogs(@RequestParam Long familyMemberId,
                                              @RequestParam(required = false) Long userId,
                                              @RequestParam(required = false) String mode,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Map.of("success", true, "data", familySafetyAdminService.listSharedLogs(familyMemberId, userId, mode, date));
    }

    @GetMapping("/medicine-profiles")
    public Map<String, Object> listMedicineProfiles(@RequestParam(required = false) Long userId,
                                                    @RequestParam(required = false) Long familyMemberId,
                                                    @RequestParam(required = false) String keyword) {
        return Map.of("success", true, "data", familySafetyAdminService.listMedicineProfiles(userId, familyMemberId, keyword));
    }

    @PostMapping("/medicine-profiles")
    public Map<String, Object> createMedicineProfile(@Valid @RequestBody MedicineProfileRequest request) {
        return Map.of("success", true, "data", familySafetyAdminService.createMedicineProfile(request));
    }

    @PutMapping("/medicine-profiles/{id}")
    public Map<String, Object> updateMedicineProfile(@PathVariable Long id, @Valid @RequestBody MedicineProfileRequest request) {
        return Map.of("success", true, "data", familySafetyAdminService.updateMedicineProfile(id, request));
    }

    @DeleteMapping("/medicine-profiles/{id}")
    public Map<String, Object> deleteMedicineProfile(@PathVariable Long id) {
        familySafetyAdminService.deleteMedicineProfile(id);
        return Map.of("success", true, "data", true);
    }
}
