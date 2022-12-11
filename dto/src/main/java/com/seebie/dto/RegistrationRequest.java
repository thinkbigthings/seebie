package com.seebie.dto;

public record RegistrationRequest(String username, String plainTextPassword, String email) { }

