package com.example.admin.service;

import com.example.admin.dto.AdminAuthResponse;
import com.example.admin.dto.AdminLoginRequest;
import com.example.admin.mapper.AdminUserMapper;
import com.example.admin.model.AdminUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_NICKNAME = "系统管理员";

    private final AdminUserMapper adminUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Value("${app.admin.default-password:123456}")
    private String defaultAdminPassword;

    @Transactional
    public AdminAuthResponse login(AdminLoginRequest request) {
        String username = request.getUsername().trim();
        if (!DEFAULT_ADMIN_USERNAME.equals(username)) {
            throw new IllegalArgumentException("管理员账号仅支持 admin");
        }

        ensureDefaultAdminExists();
        AdminUser adminUser = adminUserMapper.findByUsername(username);
        if (adminUser == null || !passwordEncoder.matches(request.getPassword(), adminUser.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (adminUser.getStatus() == null || adminUser.getStatus() != 1) {
            throw new IllegalArgumentException("管理员账号已被禁用");
        }

        LocalDateTime now = LocalDateTime.now();
        adminUserMapper.updateLastLoginAt(adminUser.getId(), now);
        String token = jwtService.createToken(adminUser.getId(), adminUser.getUsername());
        return new AdminAuthResponse(adminUser.getId(), adminUser.getUsername(), adminUser.getNickname(), token);
    }

    @Transactional
    public void ensureDefaultAdminExists() {
        if (adminUserMapper.findByUsername(DEFAULT_ADMIN_USERNAME) != null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(DEFAULT_ADMIN_USERNAME);
        adminUser.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));
        adminUser.setNickname(DEFAULT_ADMIN_NICKNAME);
        adminUser.setStatus(1);
        adminUser.setCreatedAt(now);
        adminUser.setUpdatedAt(now);
        adminUserMapper.insert(adminUser);
    }
}
