package com.seebie.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender emailSender;
    private final UserService userService;

    public NotificationService(UserService userService, JavaMailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void scanForNotifications() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("thinkbigthings@gmail.com");
        message.setTo("thinkbigthings@gmail.com");
        message.setSubject("Missing Sleep Log");
        message.setText("You missed recording your last sleep session...");

        try {
           // emailSender.send(message);
            LOG.info("Email went out here");

        }
        catch(MailException me) {
            me.printStackTrace();
        }

    }
}
