package com.seebie.server.test.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * This class is a convenience for creating test arguments for a set of roles.
 * It is used to create a list of arguments for each role, so that they can be used in a parameterized WebMvcTest
 * The advantage of this is that it allows you to test the same endpoint with different roles in the same test
 * and ensures that the same tests are run for each role.
 */
public class RoleArgumentsBuilder {

    public enum Role {
        USER, ADMIN, UNAUTHENTICATED
    }

    private final List<Arguments> unauthenticated = new ArrayList<>();
    private final List<Arguments> user = new ArrayList<>();
    private final List<Arguments> admin = new ArrayList<>();

    private final ArgumentsBuilder builder;

    public RoleArgumentsBuilder(ObjectMapper mapper) {
        this.builder = new ArgumentsBuilder(mapper);
    }

    public List<Arguments> getArguments(Role role) {
        return switch (role) {
            case USER -> this.user;
            case UNAUTHENTICATED -> this.unauthenticated;
            case ADMIN -> this.admin;
        };
    }

    // set of convenience methods to account for all roles at once

    public void post(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
        addArgs(builder.toMvcRequest(POST, urlPath, reqBody), unauthenticated, user, admin);
    }

    public void put(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
        addArgs(builder.toMvcRequest(HttpMethod.PUT, urlPath, reqBody), unauthenticated, user, admin);
    }

    public void get(String urlPath, List<String> requestParams, int unauthenticated, int user, int admin) {
        addArgs(builder.toMvcRequest(GET, urlPath, "", requestParams), unauthenticated, user, admin);
    }

    public void get(String urlPath, int unauthenticated, int user, int admin) {
        addArgs(builder.toMvcRequest(GET, urlPath, ""), unauthenticated, user, admin);
    }

    public void delete(String urlPath, int unauthenticated, int user, int admin) {
        addArgs(builder.toMvcRequest(HttpMethod.DELETE, urlPath, ""), unauthenticated, user, admin);
    }

    private void addArgs(RequestBuilder request, int unauthenticated, int user, int admin) {
        this.unauthenticated.add(Arguments.of(request, unauthenticated));
        this.user.add(Arguments.of(request, user));
        this.admin.add(Arguments.of(request, admin));
    }
}
