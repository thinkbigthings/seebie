package org.thinkbigthings.boot;

import java.io.File;
import org.springframework.boot.*;
import org.springframework.boot.actuate.system.ApplicationPidListener;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;

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
        
        File pid = new File("app.pid");
        pid.deleteOnExit();
        
        // The arguments to SpringApplication are configuration sources for spring beans. 
        // In most cases these will be references to @Configuration classes, 
        // but they could also be references to XML configuration or to packages that should be scanned
        SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);
        app.addListeners(new ApplicationPidListener(pid));
        app.run(args);
        System.out.println("================================ READY");
    }
    


}
