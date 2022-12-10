package org.thinkbigthings.zdd.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private ApiVersionInterceptor apiInterceptor;

    public WebMvcConfig(ApiVersionInterceptor apiInterceptor) {
        this.apiInterceptor = apiInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // can chain method calls to apply this to specify endpoints
        registry.addInterceptor(apiInterceptor);
    }
}