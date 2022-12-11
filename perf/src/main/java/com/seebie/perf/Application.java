package com.seebie.perf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class Application implements CommandLineRunner {

    private LoadTester tester;

    public Application(LoadTester tester) {
         this.tester = tester;
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

        System.out.println("Program done.");
    }

    @Override
    public void run(String... args) {
        tester.run();
    }
}