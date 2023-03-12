package com.seebie.server.service;

import com.seebie.server.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
public class NotificationService {

    private static Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private UserService userService;

    public NotificationService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void reportCurrentTime() {

        LOG.info("Email would be going out here");
    }
}
