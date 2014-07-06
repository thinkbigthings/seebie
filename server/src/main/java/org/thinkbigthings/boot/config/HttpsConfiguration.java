package org.thinkbigthings.boot.config;

import java.io.FileNotFoundException;
import javax.inject.Inject;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
public class HttpsConfiguration {

    /**
     * 
     * 
     * // create a keystore with this keytool -genkey -alias tomcat -storetype
     * PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12
     *
     * // view your keystore like this keytool -list -v -keystore keystore.p12
     * -storetype pkcs12
     *
     * // test with curl like so curl -u user:password -k https://127.0.0.1:9000/greeting
     *
     *
     * @param keystoreFile path to file, relative to working directory
     * @param keystorePassword
     * @param keystoreType
     * @param keystoreAlias
     * @return
     * @throws java.io.FileNotFoundException
     */
    @Bean
    @Inject
    public EmbeddedServletContainerCustomizer tomcatCustomizer(@Value("${keystore.file}") String keystoreFile,
                                                               @Value("${keystore.password}") String keystorePassword,
                                                               @Value("${keystore.type}") String keystoreType,
                                                               @Value("${keystore.alias}") String keystoreAlias) throws FileNotFoundException
    {
        // TODO try running with jetty, see if faster than tomcat
        // see https://github.com/spring-projects/spring-boot/issues/345
        // and spring boot ref guide 115-117
        
        final String absoluteKeystoreFile = ResourceUtils.getFile(keystoreFile).getAbsolutePath();
        EmbeddedServletContainerCustomizer tomcatCustomizer = (ConfigurableEmbeddedServletContainer factory) -> {
            TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) factory;
            containerFactory.addConnectorCustomizers((TomcatConnectorCustomizer) (Connector connector) -> {
                connector.setSecure(true);
                connector.setScheme("https");
                connector.setAttribute("keystoreFile", absoluteKeystoreFile);
                connector.setAttribute("keystorePass", keystorePassword);
                connector.setAttribute("keystoreType", keystoreType);
                connector.setAttribute("keyAlias", keystoreAlias);
                connector.setAttribute("clientAuth", "false");
                connector.setAttribute("sslProtocol", "TLS");
                connector.setAttribute("SSLEnabled", true);
            });
        };
        
        return tomcatCustomizer;
    }
    

}
