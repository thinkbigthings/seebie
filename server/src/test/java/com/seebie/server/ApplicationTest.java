package com.seebie.server;

import com.seebie.server.test.IntegrationTest;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * For use with bootTestRun, this class is used to run the application in a test context.
 * It should run out of the box when you run gradlew bootTestRun
 */
//@SpringBootApplication(exclude = OpenAiAutoConfiguration.class)
@EnableAutoConfiguration(exclude = OpenAiAutoConfiguration.class)
public class ApplicationTest {

    public static void main(String[] args) {

        System.out.println("Running ApplicationTest");

        SpringApplication.from(Application::main)
                .with(IntegrationTest.TestConfig.class)
                .run(args);
    }

}