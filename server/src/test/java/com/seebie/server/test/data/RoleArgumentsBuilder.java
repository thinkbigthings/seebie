package com.seebie.server.test.data;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

import static com.seebie.server.test.data.RoleArgumentsBuilder.Role.*;
import static org.springframework.http.HttpMethod.*;

/**
 * This class creates a list of arguments for each role, so that they can be used in a parameterized WebMvcTest
 * The advantage of this is that it allows you to test the same endpoint with different roles in the same test
 * and ensures that the same tests are run for each role.
 */
public class RoleArgumentsBuilder {

    public enum Role {
        USER, ADMIN, UNAUTHENTICATED
    }

    private static final List<String> NO_PARAMS = List.of();

    private final List<Arguments> unauthenticated = new ArrayList<>();
    private final List<Arguments> user = new ArrayList<>();
    private final List<Arguments> admin = new ArrayList<>();

    public List<Arguments> getArguments(Role role) {
        return switch (role) {
            case USER -> this.user;
            case UNAUTHENTICATED -> this.unauthenticated;
            case ADMIN -> this.admin;
        };
    }

    public void post(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
        addArgs(POST, urlPath, reqBody, NO_PARAMS, unauthenticated, user, admin);
    }

    public void put(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
        addArgs(PUT, urlPath, reqBody, NO_PARAMS, unauthenticated, user, admin);
    }

    public void get(String urlPath, List<String> requestParams, int unauthenticated, int user, int admin) {
        addArgs(GET, urlPath, "", requestParams, unauthenticated, user, admin);
    }

    public void get(String urlPath, int unauthenticated, int user, int admin) {
        addArgs(GET, urlPath, "", NO_PARAMS, unauthenticated, user, admin);
    }

    public void delete(String urlPath, int unauthenticated, int user, int admin) {
        addArgs(DELETE, urlPath, "", NO_PARAMS, unauthenticated, user, admin);
    }

    private void addArgs(HttpMethod method, String url, Object body, List<String> params, int unauthenticated, int user, int admin) {
        this.unauthenticated.add(Arguments.of(method, url, body, params, unauthenticated, UNAUTHENTICATED));
        this.user.add(Arguments.of(method, url, body, params, user, USER));
        this.admin.add(Arguments.of(method, url, body, params, admin, ADMIN));
    }
}
