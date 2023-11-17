package com.seebie.server;

import com.seebie.server.test.IntegrationTest;
import org.springframework.boot.SpringApplication;

public class ApplicationTest {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(IntegrationTest.TestConfig.class)
                .run(args);
    }

}