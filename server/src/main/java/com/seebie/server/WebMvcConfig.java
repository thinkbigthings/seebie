package com.seebie.server;

import org.springframework.context.annotation.Configuration;
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

    /**
     *  to use this, set   logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
     */
//    @Bean
//    public CommonsRequestLoggingFilter logFilter() {
//        var filter = new CommonsRequestLoggingFilter();
//        filter.setIncludeHeaders(true);
//        filter.setIncludeQueryString(true);
//        filter.setIncludePayload(true);
//        filter.setMaxPayloadLength(10000);
//        filter.setAfterMessagePrefix("REQUEST DATA : ");
//        return filter;
//    }
}