package com.seebie.server.test.data;

public record Response(int unauthenticated, int user, int admin) {
    public enum Role {
        USER, ADMIN, UNAUTHENTICATED
    }
    public int expected(Role role) {
        return switch (role) {
            case USER -> user;
            case ADMIN -> admin;
            case UNAUTHENTICATED -> unauthenticated;
        };
    }
}
