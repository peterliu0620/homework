package com.example.demo.service;

import com.example.demo.dto.FamilyDashboardResponse;
import com.example.demo.dto.FamilyProfileRequest;
import com.example.demo.dto.FamilyProfileResponse;
import com.example.demo.dto.FamilyRecordResponse;
import com.example.demo.mapper.FamilyMapper;
import com.example.demo.mapper.KnowledgeMapper;
import com.example.demo.mapper.MedicineSafetyMapper;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.model.FamilyUserBinding;
import com.example.demo.model.ScanItem;
import com.example.demo.model.ScanRecord;
import com.example.demo.model.SysUser;
import com.example.demo.model.UserCareProfile;
import com.example.demo.util.StringValueUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FamilyMapper familyMapper;
    private final SysUserMapper sysUserMapper;
    private final KnowledgeMapper knowledgeMapper;
    private final MedicineSafetyMapper medicineSafetyMapper;

    @Transactional(readOnly = true)
    public FamilyDashboardResponse getDashboard(Long familyUserId) {
        SysUser familyUser = requireFamilyUser(familyUserId);
        Long visionUserId = resolveVisionUserId(familyUser.getId());
        SysUser visionUser = visionUserId == null ? null : sysUserMapper.findById(visionUserId);
        int todayCount = visionUserId == null ? 0 : knowledgeMapper.countRecordsBetween(
                visionUserId,
                LocalDate.now().atStartOfDay(),
                LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        );
        int recordsCount = visionUserId == null ? 0 : knowledgeMapper.findRecentRecords(visionUserId, 20).size();
        int riskCount = visionUserId == null ? 0 : medicineSafetyMapper.countAlertsByUserId(visionUserId);
        String recentRiskText = visionUserId == null ? "当前家人账号尚未关联视障人士。"
                : defaultString(medicineSafetyMapper.findLatestAlertMessageByUserId(visionUserId), "当前暂无新的风险提醒。");
        int completedSections = countCompletedSections(visionUserId == null ? null : familyMapper.findProfileByUserId(visionUserId));
        return new FamilyDashboardResponse(
                visionUserId,
                visionUser == null ? "" : visionUser.getNickname(),
                todayCount,
                riskCount,
                recordsCount,
                completedSections,
                recentRiskText
        );
    }

    @Transactional(readOnly = true)
    public List<FamilyRecordResponse> listRecords(Long familyUserId, Integer limit) {
        requireFamilyUser(familyUserId);
        Long visionUserId = resolveVisionUserId(familyUserId);
        if (visionUserId == null) {
            return List.of();
        }
        int safeLimit = Math.max(1, Math.min(limit == null ? 10 : limit, 20));
        return knowledgeMapper.findRecentRecords(visionUserId, safeLimit).stream()
                .map(this::toFamilyRecordResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FamilyProfileResponse getProfile(Long familyUserId) {
        requireFamilyUser(familyUserId);
        Long visionUserId = resolveVisionUserId(familyUserId);
        if (visionUserId == null) {
            return new FamilyProfileResponse();
        }
        return toProfileResponse(familyMapper.findProfileByUserId(visionUserId));
    }

    @Transactional
    public FamilyProfileResponse saveProfile(Long familyUserId, FamilyProfileRequest request) {
        requireFamilyUser(familyUserId);
        Long visionUserId = resolveVisionUserId(familyUserId);
        if (visionUserId == null) {
            throw new IllegalArgumentException("当前家人账号尚未关联视障人士");
        }
        UserCareProfile profile = new UserCareProfile();
        profile.setUserId(visionUserId);
        profile.setSubjectName(trim(request.getBasicInfo().getName()));
        profile.setGender(trim(request.getBasicInfo().getGender()));
        profile.setAge(trim(request.getBasicInfo().getAge()));
        profile.setVisionLevel(trim(request.getBasicInfo().getVisionLevel()));
        profile.setAddress(trim(request.getBasicInfo().getAddress()));
        profile.setNotes(trim(request.getBasicInfo().getNotes()));
        profile.setEmergencyName(trim(request.getEmergencyContact().getName()));
        profile.setEmergencyRelation(trim(request.getEmergencyContact().getRelation()));
        profile.setEmergencyPhone(trim(request.getEmergencyContact().getPhone()));
        profile.setEmergencyBackupPhone(trim(request.getEmergencyContact().getBackupPhone()));
        profile.setMedicine(trim(request.getHealthInfo().getMedicine()));
        profile.setDiseaseNote(trim(request.getHealthInfo().getDiseaseNote()));
        profile.setAllergy(trim(request.getHealthInfo().getAllergy()));
        profile.setReminder(trim(request.getHealthInfo().getReminder()));
        familyMapper.upsertProfile(profile);
        return toProfileResponse(familyMapper.findProfileByUserId(visionUserId));
    }

    private SysUser requireFamilyUser(Long familyUserId) {
        if (familyUserId == null) {
            throw new IllegalArgumentException("家人用户ID不能为空");
        }
        SysUser user = sysUserMapper.findById(familyUserId);
        if (user == null) {
            throw new IllegalArgumentException("家人用户不存在");
        }
        if (!AuthService.ROLE_FAMILY.equals(user.getRole())) {
            throw new IllegalArgumentException("当前账号不是家人身份");
        }
        return user;
    }

    private Long resolveVisionUserId(Long familyUserId) {
        FamilyUserBinding existing = familyMapper.findBindingByFamilyUserId(familyUserId);
        if (existing != null) {
            return existing.getVisionUserId();
        }
        if (sysUserMapper.countByRole(AuthService.ROLE_VISION) == 1) {
            Long visionUserId = sysUserMapper.findFirstIdByRole(AuthService.ROLE_VISION);
            if (visionUserId != null) {
                FamilyUserBinding binding = new FamilyUserBinding();
                binding.setFamilyUserId(familyUserId);
                binding.setVisionUserId(visionUserId);
                binding.setRelationship("家人");
                familyMapper.insertBinding(binding);
            }
            return visionUserId;
        }
        return null;
    }

    private FamilyRecordResponse toFamilyRecordResponse(ScanRecord record) {
        List<ScanItem> items = knowledgeMapper.findItemsByRecordId(record.getId());
        String imageLabel = items.stream()
                .limit(2)
                .map(ScanItem::getName)
                .reduce((left, right) -> left + " / " + right)
                .orElse(defaultString(record.getScene(), "暂无"));
        boolean riskAlert = medicineSafetyMapper.countAlertsByRecordId(record.getId()) > 0;
        String riskText = riskAlert ? medicineSafetyMapper.findLatestAlertMessageByRecordId(record.getId()) : "";
        return new FamilyRecordResponse(
                record.getId(),
                record.getCapturedAt() == null ? "" : record.getCapturedAt().format(TIME_FORMATTER),
                defaultString(record.getNarration(), defaultString(record.getPositionSummary(), "已完成识别")),
                defaultString(record.getScene(), "未知场景"),
                imageLabel,
                defaultString(record.getLocationText(), "未记录位置"),
                defaultString(record.getPositionSummary(), defaultString(record.getNarration(), "")),
                riskAlert,
                defaultString(riskText, "")
        );
    }

    private FamilyProfileResponse toProfileResponse(UserCareProfile profile) {
        if (profile == null) {
            return new FamilyProfileResponse();
        }
        return new FamilyProfileResponse(
                new FamilyProfileResponse.BasicInfo(
                        defaultString(profile.getSubjectName(), ""),
                        defaultString(profile.getGender(), ""),
                        defaultString(profile.getAge(), ""),
                        defaultString(profile.getVisionLevel(), ""),
                        defaultString(profile.getAddress(), ""),
                        defaultString(profile.getNotes(), "")
                ),
                new FamilyProfileResponse.EmergencyContact(
                        defaultString(profile.getEmergencyName(), ""),
                        defaultString(profile.getEmergencyRelation(), ""),
                        defaultString(profile.getEmergencyPhone(), ""),
                        defaultString(profile.getEmergencyBackupPhone(), "")
                ),
                new FamilyProfileResponse.HealthInfo(
                        defaultString(profile.getMedicine(), ""),
                        defaultString(profile.getDiseaseNote(), ""),
                        defaultString(profile.getAllergy(), ""),
                        defaultString(profile.getReminder(), "")
                )
        );
    }

    private int countCompletedSections(UserCareProfile profile) {
        if (profile == null) {
            return 0;
        }
        int count = 0;
        if (StringValueUtils.trimToNull(profile.getSubjectName()) != null && StringValueUtils.trimToNull(profile.getAge()) != null) {
            count += 1;
        }
        if (StringValueUtils.trimToNull(profile.getEmergencyName()) != null && StringValueUtils.trimToNull(profile.getEmergencyPhone()) != null) {
            count += 1;
        }
        if (StringValueUtils.trimToNull(profile.getMedicine()) != null || StringValueUtils.trimToNull(profile.getDiseaseNote()) != null) {
            count += 1;
        }
        return count;
    }

    private String trim(String value) {
        return StringValueUtils.trimToNull(value);
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
