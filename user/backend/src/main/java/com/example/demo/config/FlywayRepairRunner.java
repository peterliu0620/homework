package com.example.demo.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayRepairRunner {

    @Bean
    @ConditionalOnProperty(name = "app.flyway.repair-on-start", havingValue = "true")
    public CommandLineRunner repairCommand(DataSource dataSource) {
        return args -> {
            Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load()
                    .repair();
            System.out.println("Flyway repair completed.");
            System.exit(0);
        };
    }
}
