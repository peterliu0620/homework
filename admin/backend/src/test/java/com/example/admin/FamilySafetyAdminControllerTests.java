package com.example.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FamilySafetyAdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSupportFamilyMemberCrudBindingQueryAndUnbind() throws Exception {
        String token = loginAndExtractToken();
        jdbcTemplate.update("delete from medicine_profile");
        jdbcTemplate.update("delete from family_binding");
        jdbcTemplate.update("delete from family_member");
        jdbcTemplate.update("delete from `user`");
        jdbcTemplate.update("""
                insert into `user`(id, username, password_hash, nickname, phone, email, status, created_at, updated_at)
                values(101, 'blind-user-1', 'hash', '视障用户1', '13800138011', 'blind1@example.com', 1, now(), now())
                """);
        jdbcTemplate.update("""
                insert into `user`(id, username, password_hash, nickname, phone, email, status, created_at, updated_at)
                values(102, 'blind-user-2', 'hash', '视障用户2', '13800138012', 'blind2@example.com', 1, now(), now())
                """);

        String createResponse = mockMvc.perform(post("/api/admin/family-members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "李家属",
                                  "phone": "13900139011",
                                  "email": "family1@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("李家属"))
                .andExpect(jsonPath("$.data.bindingCount").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long familyMemberId = extractLongField(createResponse, "\"id\":");

        mockMvc.perform(put("/api/admin/family-members/" + familyMemberId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "李家属-更新",
                                  "phone": "13900139012",
                                  "email": "family1-updated@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("李家属-更新"))
                .andExpect(jsonPath("$.data.phone").value("13900139012"));

        mockMvc.perform(post("/api/admin/family-members/" + familyMemberId + "/bindings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 101,
                                  "relationship": "女儿",
                                  "status": "active"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.familyMemberId").value(familyMemberId))
                .andExpect(jsonPath("$.data.userId").value(101))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        mockMvc.perform(post("/api/admin/family-members/" + familyMemberId + "/bindings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 102,
                                  "relationship": "监护人"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(102));

        mockMvc.perform(get("/api/admin/family-members")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", "李家属"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bindingCount").value(2));

        mockMvc.perform(get("/api/admin/family-members/" + familyMemberId + "/blind-users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].familyName").value("李家属-更新"));

        mockMvc.perform(get("/api/admin/blind-users/101/family-members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(101));

        mockMvc.perform(delete("/api/admin/family-members/" + familyMemberId + "/bindings/101")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        mockMvc.perform(get("/api/admin/family-members/" + familyMemberId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bindingCount").value(1));

        mockMvc.perform(delete("/api/admin/family-members/" + familyMemberId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void shouldSupportFamilyBindingMedicineCrudAndSharedLogs() throws Exception {
        String token = loginAndExtractToken();
        jdbcTemplate.update("""
                insert into `user`(id, username, password_hash, nickname, phone, email, status, created_at, updated_at)
                values(10, 'vision-user', 'hash', '视障用户', '13800138001', 'vision@example.com', 1, now(), now())
                """);

        mockMvc.perform(post("/api/admin/family-bindings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 10,
                                  "familyName": "王家属",
                                  "familyPhone": "13900139001",
                                  "familyEmail": "family@example.com",
                                  "relationship": "女儿",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.familyName").value("王家属"))
                .andExpect(jsonPath("$.data.username").value("vision-user"));

        mockMvc.perform(post("/api/admin/medicine-profiles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 10,
                                  "familyMemberId": 1,
                                  "medicineName": "阿莫西林胶囊",
                                  "genericName": "阿莫西林",
                                  "dosageUsage": "一次两粒，每日三次",
                                  "suitablePeople": "成人",
                                  "contraindications": "青霉素过敏者禁用",
                                  "expiryDate": "2026-12-31",
                                  "barcodeOrAlias": "阿莫西林,amoxicillin"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.medicineName").value("阿莫西林胶囊"));

        mockMvc.perform(put("/api/admin/medicine-profiles/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 10,
                                  "familyMemberId": 1,
                                  "medicineName": "阿莫西林胶囊",
                                  "genericName": "阿莫西林",
                                  "dosageUsage": "一次一粒，每日三次",
                                  "suitablePeople": "成人",
                                  "contraindications": "青霉素过敏者禁用",
                                  "expiryDate": "2026-12-31",
                                  "barcodeOrAlias": "阿莫西林"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dosageUsage").value("一次一粒，每日三次"));

        jdbcTemplate.update("""
                insert into scan_record(id, user_id, session_id, mode, trigger_command, scene, narration, position_summary,
                captured_at, latitude, longitude, location_text, created_at)
                values(20, 10, 'session-1', 'analyze', '识别药品', '卧室', '这是阿莫西林胶囊', '右手边', ?, 0, 0, '客厅药柜', now())
                """, LocalDateTime.now());
        jdbcTemplate.update("""
                insert into scan_item(id, record_id, name, position, confidence, attributes_json, is_primary)
                values(21, 20, '阿莫西林胶囊', '画面中心', 96, '{}', 1)
                """);
        jdbcTemplate.update("""
                insert into medicine_scan_alert(id, user_id, scan_record_id, medicine_profile_id, alert_type, alert_message, created_at)
                values(30, 10, 20, 1, 'repeat_scan', '短时重复扫码，请联系家属确认。', now())
                """);

        mockMvc.perform(get("/api/admin/shared-logs")
                        .header("Authorization", "Bearer " + token)
                        .param("familyMemberId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("vision-user"))
                .andExpect(jsonPath("$.data[0].alertTriggered").value(true));

        mockMvc.perform(get("/api/admin/medicine-profiles")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].medicineName").value("阿莫西林胶囊"));

        mockMvc.perform(delete("/api/admin/medicine-profiles/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    private String loginAndExtractToken() throws Exception {
        String response = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int start = response.indexOf("\"token\":\"");
        int from = start + 9;
        int end = response.indexOf('"', from);
        return response.substring(from, end);
    }

    private long extractLongField(String response, String marker) {
        int start = response.indexOf(marker);
        int from = start + marker.length();
        int end = from;
        while (end < response.length() && Character.isDigit(response.charAt(end))) {
            end++;
        }
        return Long.parseLong(response.substring(from, end));
    }
}
