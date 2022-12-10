package org.thinkbigthings.zdd.dto;

public record RegistrationRequest(String username, String plainTextPassword, String email) { }

