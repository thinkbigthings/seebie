package com.seebie.server;

import com.seebie.server.test.IntegrationTest;
import org.springframework.boot.SpringApplication;

/**
 * For use with springBootTest, this class is used to run the application in a test context.
 */
public class ApplicationTest {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(IntegrationTest.TestConfig.class)
                .run(args);
    }

}