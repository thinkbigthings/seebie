package com.seebie.server.dto;

public record RegistrationRequest(String username, String plainTextPassword, String email) { }

