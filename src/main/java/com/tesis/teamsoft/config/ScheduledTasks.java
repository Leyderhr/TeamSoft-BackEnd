package com.tesis.teamsoft.config;

import com.tesis.teamsoft.service.implementation.RefreshTokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    private RefreshTokenServiceImpl refreshTokenService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");
        refreshTokenService.deleteExpiredTokens();
    }
}
