package com.seebie.server.service;

/**
 * The publicId is here so that we can build a url for the user's email to go directly to their page.
 */
public record NotificationRequired(String email, String username) {
}
