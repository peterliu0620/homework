package com.example.admin.config;

import com.example.admin.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminInitializationConfig {

    private final AdminAuthService adminAuthService;

    @Bean
    public CommandLineRunner adminInitializer() {
        return args -> adminAuthService.ensureDefaultAdminExists();
    }
}
