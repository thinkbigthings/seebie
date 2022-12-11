package com.seebie.perf;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class BasicAuthenticator extends Authenticator {

    private PasswordAuthentication authentication;

    public BasicAuthenticator(String username, String password) {
        authentication = new PasswordAuthentication(username, password.toCharArray());
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }
}
