package org.thinkbigthings.zdd.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import static java.util.Optional.ofNullable;

/**
 * This adds a Version header on requests for the API and for static resources.
 *
 * However, Interceptors are not called on requests to actuator endpoints.
 * To intercept those you'd need to do more configuration work or use a Filter.
 * https://github.com/spring-projects/spring-boot/issues/11234
 *
 * If during development the UI is being served by a UI dev environment (e.g. npm start, Express)
 * then you won't see that header in requests for static resources.
 */
@Component
public class ApiVersionInterceptor implements HandlerInterceptor {

    private static Logger LOG = LoggerFactory.getLogger(ApiVersionInterceptor.class);

    public static final String API_VERSION  = "X-Version";

    private final String apiVersion;

    public ApiVersionInterceptor(AppProperties config) {
        apiVersion = config.apiVersion().toString();
    }

    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return true if the execution chain should proceed with the next interceptor or the handler itself.
     * Else, DispatcherServlet assumes that this interceptor has already dealt with the response itself.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        response.setHeader(API_VERSION, apiVersion);

        // can be null, if it's missing then act as if it's the latest
        String requestApiVersion = ofNullable(request.getHeader(API_VERSION)).orElse(apiVersion);

        // since this is an Interceptor and not a Filter,
        // in theory we could inspect handler annotations for what version it can handle.
        if( ! requestApiVersion.equals(apiVersion)) {
            String message = "Request api " + requestApiVersion + " does not match server api " + apiVersion;
            LOG.error(message);
            throw new IncompatibleClientVersionException(message);
        }

        return true;
    }

}


