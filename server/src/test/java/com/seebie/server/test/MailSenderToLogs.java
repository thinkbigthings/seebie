package com.seebie.server.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Arrays;

public class MailSenderToLogs implements MailSender {
    private static Logger LOG = LoggerFactory.getLogger(MailSenderToLogs.class);

    @Override
    public void send(SimpleMailMessage message) throws MailException {
        LOG.info("Sending email: " + message);
    }

    @Override
    public void send(SimpleMailMessage... messages) throws MailException {
        Arrays.stream(messages).forEach(this::send);
    }
}
