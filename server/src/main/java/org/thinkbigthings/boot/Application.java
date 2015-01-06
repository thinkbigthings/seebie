package org.thinkbigthings.boot;

import org.springframework.boot.*;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;


// TODO 2 should generate test reports for both unit and integration tests when doing full build
// (right now last test run is all that remains)

// TODO 2 shouldn't restart server for intTest if no code changes were made
// although this could be a problem to detect if you do a gradle clean build; gradle intTest

// TODO 2 should not continue integration test if compile fails.

// TODO 3 investigate security headers
// http://www.ibuildings.com/blog/2013/03/4-http-security-headers-you-should-always-be-using

// TODO 5 try running with jetty, see if faster than tomcat
// see https://github.com/spring-projects/spring-boot/issues/345
// and spring boot ref guide 115-117

/*
 @EnableAutoconfiguration tells Spring Boot to "guess" how you will want 
to configure Spring, based on the jar dependencies that you have added. 
Since spring-boot-starter-web added Tomcat and Spring MVC, the auto-configuration
will assume that you are developing a web application and setup Spring accordingly
 */
@EnableAutoConfiguration
@ComponentScan
public class Application {

    public static void main(String[] args) throws Exception {

        // The arguments to SpringApplication are configuration sources for spring beans. 
        // In most cases these will be references to @Configuration classes, 
        // but they could also be references to XML configuration or to packages that should be scanned
        SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);
        app.addListeners(new ApplicationPidFileWriter("app.pid"));
        app.run(args);
        System.out.println("================================ READY");
    }
    


}
